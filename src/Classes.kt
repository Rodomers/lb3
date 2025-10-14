interface IPayCalculationStrategy {
    val name: String
    fun calculate(works: List<Work>): Double
}

class StandardCalculationStrategy : IPayCalculationStrategy {
    override val name: String = "Стандартная оплата"

    override fun calculate(works: List<Work>): Double {
        return works.sumOf { it.calculateTotalPay() }
    }
}

class PremiumCalculationStrategy : IPayCalculationStrategy {
    override val name: String = "Повышенная оплата (+15%)"

    override fun calculate(works: List<Work>): Double {
        val baseSum = works.sumOf { it.calculateTotalPay() }
        return baseSum * 1.15
    }
}

class FixedBonusCalculationStrategy : IPayCalculationStrategy {
    override val name: String = "Стандартная с фиксированным бонусом (+200)"

    override fun calculate(works: List<Work>): Double {
        return works.sumOf { it.calculateTotalPay() } + 200.0
    }
}

data class Work(val name: String, val pay: Double) {
    fun calculateTotalPay(): Double {
        return pay
    }
}

class Employee(val surname: String) {
    val works = mutableListOf<Work>()

    var calculationStrategy: IPayCalculationStrategy = StandardCalculationStrategy()

    fun addWork(work: Work) {
        works.add(work)
    }

    fun countFee(): Double {
        return calculationStrategy.calculate(works)
    }
}

object PayrollDepartment {
    private val employees = mutableListOf<Employee>()
    private val workTypes = mutableListOf<Work>()

    private fun getEmployee(surname: String): Employee? {
        return employees.find { it.surname.equals(surname, ignoreCase = true) }
    }

    fun addEmployee(employee: Employee): Boolean {
        if (getEmployee(employee.surname) == null && employee.surname.isNotEmpty()) {
            employees.add(employee)
            return true
        }
        return false
    }

    fun deleteEmployee(surname: String): Boolean {
        return employees.removeIf { it.surname.equals(surname, ignoreCase = true) }
    }

    fun addWorkType(work: Work): Boolean {
        if (workTypes.none { it.name.equals(work.name, ignoreCase = true) } && work.name.isNotEmpty()) {
            workTypes.add(work)
            return true
        }
        return false
    }

    fun addEmployeeDoneWork(surname: String, workName: String): Boolean {
        val employee = getEmployee(surname)
        val work = workTypes.find { it.name.equals(workName, ignoreCase = true) }

        if (employee != null && work != null) {
            employee.addWork(work)
            return true
        }
        return false
    }

    fun countEmployeeFee(surname: String): Pair<Double, String>? {
        val employee = getEmployee(surname)
        return if (employee != null) {
            Pair(employee.countFee(), employee.calculationStrategy.name)
        } else {
            null
        }
    }

    fun setEmployeeStrategy(surname: String, strategyChoice: Int): Boolean {
        val employee = getEmployee(surname) ?: return false

        val newStrategy: IPayCalculationStrategy = when (strategyChoice) {
            1 -> StandardCalculationStrategy()
            2 -> PremiumCalculationStrategy()
            3 -> FixedBonusCalculationStrategy()
            else -> return false
        }

        employee.calculationStrategy = newStrategy
        return true
    }

    fun countFeesSum(): Double {
        return employees.sumOf { it.countFee() }
    }

    fun calculateAverageSalary(): Double {
        if (employees.isEmpty()) return 0.0
        return countFeesSum() / employees.size
    }

    fun printWorkTypes(full: Boolean = false): String {
        if (workTypes.isEmpty()) return "Виды работ не добавлены"
        return when (full) {
            true -> workTypes.joinToString("\n") { work ->
                "${work.name}: оплата ${work.pay} м.к."
            }
            else -> workTypes.joinToString(", ") { it.name }
        }
    }

    fun printEmployees(): String {
        if (employees.isEmpty()) return "Работники не добавлены"
        return employees.joinToString(", ") { "${it.surname} (${it.calculationStrategy.name})" }
    }

    fun checkEmployee(surname: String): Boolean {
        return getEmployee(surname) != null
    }
}