package com.fincalc.app.data

/** Single registry for all 60 calculators. */
object CalculatorRegistry {

    val all: List<CalculatorDef> =
        CalculatorsLoan.all + CalculatorsTax.all + CalculatorsInvest.all +
            CalculatorsPersonal.all + CalculatorsBusiness.all + CalculatorsDaily.all

    fun byId(id: String): CalculatorDef? = all.find { it.id == id }

    fun byCategory(category: Category): List<CalculatorDef> =
        all.filter { it.category == category }

    fun popular(): List<CalculatorDef> = all.filter { it.popular }

    fun quickTools(): List<CalculatorDef> = all.filter { it.quick }

    fun search(query: String): List<CalculatorDef> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        return all.filter {
            it.title.lowercase().contains(q) ||
                it.description.lowercase().contains(q) ||
                it.category.title.lowercase().contains(q)
        }
    }
}
