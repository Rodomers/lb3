import java.util.Scanner
import kotlin.system.exitProcess

val scan = Scanner(System.`in`)

fun clearConsole(fake: Boolean = true) {
    if (fake) for (i in 0..1) println() else print("\u001b[H\u001b[2J")
}

fun addWorkMenu() {
    clearConsole()
    while (true) {
        println("Введите название работы или 0 для выхода")
        val name = scan.nextLine().trim()
        if (name == "0") {
            mainMenu()
            break
        }
        if (name.isEmpty()) {
            println("Название работы не может быть пустым.")
            continue
        }

        var basePay: Double? = null
        while (basePay == null) {
            println("Введите стоимость работы '$name':")
            try {
                basePay = scan.nextLine().trim().toDouble()
                if (basePay < 0) {
                    println("Оплата не может быть отрицательной.")
                    basePay = null
                }
            } catch (e: NumberFormatException) {
                println("Ошибка: введите корректное число.")
            }
        }

        if (PayrollDepartment.addWorkType(Work(name, basePay))) {
            println("Новый вид работ '$name' успешно добавлен.")
        } else {
            println("Работа с таким названием уже существует.")
        }
        mainMenu()
        break
    }
}

fun employeeMenu(
    del: Boolean = false,
    addWork: Boolean = false,
    countFee: Boolean = false
) {
    val additionalStr = when {
        del -> "Вы выбрали удаление работника."
        addWork -> "Кем была выполнена работа?"
        countFee -> "Вы выбрали подсчёт выплаты работнику."
        else -> "Вы выбрали добавление нового работника."
    }

    clearConsole()
    println("$additionalStr\nСписок работников предприятия:\n${PayrollDepartment.printEmployees()}.\n")
    println("Введите фамилию работника или 0 для выхода")

    val line = scan.nextLine()
    var res: String? = null
    if (line != "0") {
        val name = line.trim()
        when {
            del -> {
                res = if (PayrollDepartment.deleteEmployee(name)) "$name уволен(а)."
                else "Работник с такой фамилией не найден."
            }
            addWork -> {
                if (!PayrollDepartment.checkEmployee(name)) res = "Такого работника не существует"
                else {
                    println(
                        "Существующие в компании виды работ: ${PayrollDepartment.printWorkTypes()}\n" +
                                "Введите вид работы, который выполнил(а) $name."
                    )
                    val type = scan.nextLine().trim()
                    res = if (PayrollDepartment.addEmployeeDoneWork(name, type)) "Работа добавлена для $name."
                    else "Такого вида работ не существует."
                }
            }
            countFee -> {
                val feeResult = PayrollDepartment.countEmployeeFee(name)
                res = when (feeResult) {
                    null -> "Такого работника не существует."
                    else -> "Сумма выплат для работника $name: ${feeResult.first} (по стратегии '${feeResult.second}')"
                }
            }
            else -> {
                res = if (PayrollDepartment.addEmployee(Employee(name))) "Работник $name добавлен."
                else "Работник с такой фамилией уже существует или фамилия пуста."
            }
        }
    }
    mainMenu(text = res ?: "")
}

fun changeStrategyMenu() {
    clearConsole()
    println("Выберите работника, чтобы изменить его схему расчета зарплаты.")
    println("Список работников:\n${PayrollDepartment.printEmployees()}\n")
    println("Введите фамилию работника или 0 для выхода")

    val surname = scan.nextLine().trim()
    if (surname == "0" || surname.isEmpty()) {
        mainMenu()
        return
    }

    if (!PayrollDepartment.checkEmployee(surname)) {
        mainMenu("Работник с фамилией '$surname' не найден.")
        return
    }

    println("\nВыберите новую схему расчета для работника $surname:")
    println("1. Стандартная (базовая оплата + надбавки за работу)")
    println("2. Премиальная (общая сумма + 15% премии)")
    println("3. С фиксированным бонусом (общая сумма + 200)")

    val choice = try { scan.nextLine().trim().toInt() } catch (e: Exception) { -1 }

    if (PayrollDepartment.setEmployeeStrategy(surname, choice)) {
        mainMenu("Схема расчета для работника $surname успешно изменена.")
    } else {
        mainMenu("Неверный выбор схемы.")
    }
}

fun workTypesMenu() {
    clearConsole()
    println(
        "Список видов работ, доступных на предприятии:\n${PayrollDepartment.printWorkTypes(full = true)}\n\n" +
                "Нажмите Enter для возврата в главное меню"
    )
    scan.nextLine()
    mainMenu()
}

fun mainMenu(text: String = "") {
    clearConsole()
    if (text.isNotEmpty()) println("$text\n")

    println("Меню:")
    println("1. Добавить вид работы")
    println("2. Добавить работника")
    println("3. Удалить работника")
    println("4. Добавить выполненную работу")
    println("5. Изменить схему расчета для работника")
    println("------------------------------------")
    println("6. Посчитать зарплату работника")
    println("7. Посчитать общую сумму выплат")
    println("8. Вычислить среднюю зарплату")
    println("------------------------------------")
    println("9. Получить список работников предприятия")
    println("10. Получить список видов работ")
    println("0. Выход из программы")

    val choice = try { scan.nextLine().toInt() } catch (e: Exception) { -1 }

    when (choice) {
        1 -> addWorkMenu()
        2 -> employeeMenu()
        3 -> employeeMenu(del = true)
        4 -> employeeMenu(addWork = true)
        5 -> changeStrategyMenu()
        6 -> employeeMenu(countFee = true)
        7 -> mainMenu("Общая сумма выплат работников: ${PayrollDepartment.countFeesSum()}.")
        8 -> mainMenu("Средняя величина оплаты по предприятию: ${PayrollDepartment.calculateAverageSalary()}")
        9 -> mainMenu("Список работников предприятия:\n${PayrollDepartment.printEmployees()}")
        10 -> workTypesMenu()
        0 -> { clearConsole(); println("Выход..."); exitProcess(0) }
        else -> mainMenu("Неверный ввод. Пожалуйста, выберите пункт меню.")
    }
}

fun main() {
    mainMenu()
}