package za.co.quikle.lexio.ui.screens.library

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import za.co.quikle.lexio.domain.model.LegalCitation
import za.co.quikle.lexio.domain.model.RightsCategory
import za.co.quikle.lexio.domain.model.RightsTopic

data class LibraryUiState(
    val categories: List<RightsCategory> = emptyList(),
    val filteredCategories: List<RightsCategory> = emptyList(),
    val selectedCategory: RightsCategory? = null,
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val isSearchVisible: Boolean = false
)

class LibraryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        val categories = buildMockCategories()
        _uiState.value = LibraryUiState(
            categories = categories,
            filteredCategories = categories
        )
    }

    fun onSearchQueryChanged(query: String) {
        val filtered = if (query.isBlank()) {
            _uiState.value.categories
        } else {
            _uiState.value.categories.mapNotNull { category ->
                val matchingTopics = category.topics.filter { topic ->
                    topic.title.contains(query, ignoreCase = true) ||
                            topic.summary.contains(query, ignoreCase = true) ||
                            topic.legislation.any { it.shortReference.contains(query, ignoreCase = true) }
                }
                val categoryMatches = category.name.contains(query, ignoreCase = true) ||
                        category.description.contains(query, ignoreCase = true)
                when {
                    categoryMatches -> category
                    matchingTopics.isNotEmpty() -> category.copy(topics = matchingTopics)
                    else -> null
                }
            }
        }
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredCategories = filtered
        )
    }

    fun toggleSearch() {
        val newVisible = !_uiState.value.isSearchVisible
        _uiState.value = _uiState.value.copy(
            isSearchVisible = newVisible,
            searchQuery = if (!newVisible) "" else _uiState.value.searchQuery,
            filteredCategories = if (!newVisible) _uiState.value.categories else _uiState.value.filteredCategories
        )
    }

    fun selectCategory(categoryId: String) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = _uiState.value.categories.find { it.id == categoryId }
        )
    }

    fun getCategoryById(categoryId: String): RightsCategory? {
        return _uiState.value.categories.find { it.id == categoryId }
    }

    private fun buildMockCategories(): List<RightsCategory> = listOf(
        buildWorkplaceRights(),
        buildConsumerProtection(),
        buildHousingProperty(),
        buildConstitutionalRights(),
        buildCriminalLaw(),
        buildFamilyLaw(),
        buildPrivacyData(),
        buildRoadTraffic()
    )

    // ── Workplace Rights (8 topics) ──────────────────────────────────────

    private fun buildWorkplaceRights(): RightsCategory = RightsCategory(
        id = "workplace",
        name = "Workplace Rights",
        description = "Your rights as an employee under South African labour law",
        iconName = "work",
        topics = listOf(
            RightsTopic(
                id = "wp_unfair_dismissal",
                title = "Unfair Dismissal",
                summary = "Every employee has the right not to be unfairly dismissed. Your employer must have a valid reason (such as misconduct, incapacity, or operational requirements) and must follow a fair procedure before terminating your employment.\n\nIf you believe you were unfairly dismissed, you can refer the dispute to the CCMA within 30 days of the dismissal. The CCMA will attempt conciliation, and if that fails, the matter may proceed to arbitration.\n\nAn automatically unfair dismissal occurs when you are dismissed for reasons such as pregnancy, trade union membership, or exercising a legal right. These carry stronger protections and potential reinstatement.",
                legislation = listOf(
                    LegalCitation(actName = "Labour Relations Act", actNumber = "Act 66 of 1995", section = "Section 188", fullReference = "LRA s188", shortReference = "LRA s188", fullText = "188. Other unfair dismissals.—(1) A dismissal that is not automatically unfair, is unfair if the employer fails to prove—(a) that the reason for dismissal is a fair reason—(i) related to the employee's conduct or capacity; or (ii) based on the employer's operational requirements; and (b) that the dismissal was effected in accordance with a fair procedure."),
                    LegalCitation(actName = "Labour Relations Act", actNumber = "Act 66 of 1995", section = "Section 189", fullReference = "LRA s189", shortReference = "LRA s189", fullText = "189. Dismissals based on operational requirements.—(1) When an employer contemplates dismissing one or more employees for reasons based on the employer's operational requirements, the employer must consult—(a) any person whom the employer is required to consult in terms of a collective agreement...")
                ),
                fullLegalText = "Section 188 of the LRA provides that a dismissal is unfair if the employer fails to prove that the reason for dismissal is fair (related to conduct, capacity, or operational requirements) and that a fair procedure was followed. Section 189 sets out the consultation process required for retrenchments.",
                relatedTopicIds = listOf("wp_ccma", "wp_discrimination")
            ),
            RightsTopic(
                id = "wp_working_hours",
                title = "Working Hours & Overtime",
                summary = "Ordinary working hours may not exceed 45 hours per week (9 hours per day for a 5-day week, or 8 hours per day for a 6-day week). Any work beyond these hours is overtime.\n\nOvertime is voluntary — your employer cannot force you to work overtime unless your contract specifically provides for it. Overtime must be paid at 1.5 times your normal wage, or at double time on Sundays and public holidays.\n\nYou may not work more than 10 hours of overtime per week, and no more than 12 hours on any single day (including normal hours).",
                legislation = listOf(
                    LegalCitation(actName = "Basic Conditions of Employment Act", actNumber = "Act 75 of 1997", section = "Section 9", fullReference = "BCEA s9", shortReference = "BCEA s9", fullText = "9. Ordinary hours of work.—(1) Subject to this Chapter, an employer may not require or permit an employee to work more than—(a) 45 hours in any week; (b) nine hours in any day if the employee works for five days or fewer in a week; or (c) eight hours in any day if the employee works on more than five days in a week."),
                    LegalCitation(actName = "Basic Conditions of Employment Act", actNumber = "Act 75 of 1997", section = "Section 10", fullReference = "BCEA s10", shortReference = "BCEA s10", fullText = "10. Overtime.—(1) Subject to this Chapter, an employer may not require or permit an employee—(a) to work overtime except in accordance with an agreement; (b) to work more than ten hours' overtime a week.")
                ),
                fullLegalText = "The BCEA limits ordinary working hours to 45 per week. Overtime requires agreement and is capped at 10 hours per week. Overtime pay is at least 1.5 times the normal rate.",
                relatedTopicIds = listOf("wp_annual_leave", "wp_minimum_wage")
            ),
            RightsTopic(
                id = "wp_annual_leave",
                title = "Annual Leave",
                summary = "Every employee is entitled to at least 21 consecutive days of paid annual leave per year, or by agreement, one day for every 17 days worked, or one hour for every 17 hours worked.\n\nAnnual leave must be granted not later than six months after the end of the annual leave cycle. Your employer may not pay you instead of granting leave, except on termination of employment.\n\nYour employer must pay you your normal wage during annual leave, and this payment must be made before the leave period starts.",
                legislation = listOf(
                    LegalCitation(actName = "Basic Conditions of Employment Act", actNumber = "Act 75 of 1997", section = "Section 20", fullReference = "BCEA s20", shortReference = "BCEA s20", fullText = "20. Annual leave.—(1) An employer must grant an employee at least—(a) 21 consecutive days' annual leave on full remuneration in respect of each annual leave cycle; or (b) by agreement, one day of annual leave on full remuneration for every 17 days on which the employee worked or was entitled to be paid...")
                ),
                fullLegalText = "Section 20 of the BCEA guarantees at least 21 consecutive days of paid annual leave per leave cycle. Leave may not be replaced by payment except upon termination.",
                relatedTopicIds = listOf("wp_working_hours", "wp_maternity_leave")
            ),
            RightsTopic(
                id = "wp_minimum_wage",
                title = "Minimum Wage",
                summary = "South Africa has a national minimum wage that applies to all workers. As of 2024, the national minimum wage is R27.58 per hour. This is reviewed annually by the National Minimum Wage Commission.\n\nCertain sectors have different rates: farm workers and domestic workers are now covered by the same national minimum wage. Workers on expanded public works programmes have a lower rate.\n\nIf your employer pays you less than the minimum wage, you can report this to the Department of Employment and Labour or refer the matter to the CCMA.",
                legislation = listOf(
                    LegalCitation(actName = "National Minimum Wage Act", actNumber = "Act 9 of 2018", section = "Section 3", fullReference = "NMWA s3", shortReference = "NMWA s3", fullText = "3. National minimum wage.—(1) A worker is entitled to be paid a wage that is not less than the national minimum wage.")
                ),
                fullLegalText = "The National Minimum Wage Act establishes a floor wage for all workers in South Africa. The rate is adjusted annually based on recommendations from the National Minimum Wage Commission.",
                relatedTopicIds = listOf("wp_working_hours")
            ),
            RightsTopic(
                id = "wp_maternity_leave",
                title = "Maternity Leave",
                summary = "A pregnant employee is entitled to at least four consecutive months of maternity leave. She may begin maternity leave at any time from four weeks before the expected date of birth, or on a date a medical practitioner or midwife certifies.\n\nAn employee may not work for six weeks after the birth of her child, unless a medical practitioner or midwife certifies that she is fit to do so.\n\nWhile the BCEA does not require paid maternity leave, employees who contribute to the Unemployment Insurance Fund (UIF) can claim maternity benefits from the UIF during their leave period.",
                legislation = listOf(
                    LegalCitation(actName = "Basic Conditions of Employment Act", actNumber = "Act 75 of 1997", section = "Section 25", fullReference = "BCEA s25", shortReference = "BCEA s25", fullText = "25. Maternity leave.—(1) An employee is entitled to at least four consecutive months' maternity leave. (2) An employee may commence maternity leave—(a) at any time from four weeks before the expected date of birth, unless otherwise agreed; or (b) on a date from which a medical practitioner or a midwife certifies that it is necessary for the employee's health or that of her unborn child.")
                ),
                fullLegalText = "Section 25 of the BCEA provides for at least four consecutive months of maternity leave. The employee may not work for six weeks after birth unless medically certified fit.",
                relatedTopicIds = listOf("wp_annual_leave", "wp_unfair_dismissal")
            ),
            RightsTopic(
                id = "wp_ccma",
                title = "CCMA Process",
                summary = "The Commission for Conciliation, Mediation and Arbitration (CCMA) is a dispute resolution body that helps resolve workplace disputes. You can refer a dispute to the CCMA if you have been unfairly dismissed, subjected to an unfair labour practice, or if there is a dispute about wages or working conditions.\n\nThe process begins with conciliation, where a commissioner tries to help the parties reach a settlement. If conciliation fails, the matter may proceed to arbitration, where the commissioner makes a binding decision.\n\nYou must refer an unfair dismissal dispute within 30 days of the dismissal. There is no cost to refer a dispute to the CCMA.",
                legislation = listOf(
                    LegalCitation(actName = "Labour Relations Act", actNumber = "Act 66 of 1995", section = "Section 133", fullReference = "LRA s133", shortReference = "LRA s133", fullText = "133. Establishment of Commission for Conciliation, Mediation and Arbitration.—The Commission for Conciliation, Mediation and Arbitration is hereby established as a juristic person."),
                    LegalCitation(actName = "Labour Relations Act", actNumber = "Act 66 of 1995", section = "Section 134", fullReference = "LRA s134", shortReference = "LRA s134", fullText = "134. Independence of Commission.—The Commission is independent of the State, any political party, trade union, employer, employers' organisation, federation of trade unions or federation of employers' organisations."),
                    LegalCitation(actName = "Labour Relations Act", actNumber = "Act 66 of 1995", section = "Section 135", fullReference = "LRA s135", shortReference = "LRA s135", fullText = "135. Functions of Commission.—The Commission—(a) must attempt to resolve, through conciliation, any dispute referred to it in terms of this Act...")
                ),
                fullLegalText = "Sections 133–135 of the LRA establish the CCMA as an independent body for resolving labour disputes through conciliation and arbitration. The CCMA's services are free of charge.",
                relatedTopicIds = listOf("wp_unfair_dismissal", "wp_discrimination")
            ),
            RightsTopic(
                id = "wp_discrimination",
                title = "Workplace Discrimination",
                summary = "No person may unfairly discriminate against an employee on the grounds of race, gender, sex, pregnancy, marital status, family responsibility, ethnic or social origin, colour, sexual orientation, age, disability, religion, HIV status, conscience, belief, political opinion, culture, language, birth, or any other arbitrary ground.\n\nThe Employment Equity Act requires designated employers to implement affirmative action measures to achieve equitable representation in the workplace.\n\nIf you experience discrimination, you can refer the matter to the CCMA or the Labour Court. Harassment, including sexual harassment, is a form of unfair discrimination.",
                legislation = listOf(
                    LegalCitation(actName = "Employment Equity Act", actNumber = "Act 55 of 1998", section = "Section 6", fullReference = "EEA s6", shortReference = "EEA s6", fullText = "6. Prohibition of unfair discrimination.—(1) No person may unfairly discriminate, directly or indirectly, against an employee, in any employment policy or practice, on one or more grounds, including race, gender, sex, pregnancy, marital status, family responsibility, ethnic or social origin, colour, sexual orientation, age, disability, religion, HIV status, conscience, belief, political opinion, culture, language, birth or on any other arbitrary ground.")
                ),
                fullLegalText = "Section 6 of the EEA prohibits unfair discrimination in any employment policy or practice on listed grounds. The burden of proof shifts to the employer to show that discrimination was fair.",
                relatedTopicIds = listOf("wp_unfair_dismissal", "wp_ccma")
            ),
            RightsTopic(
                id = "wp_strike",
                title = "Right to Strike",
                summary = "Every employee has the right to strike, provided the proper procedures are followed. A protected strike requires that the dispute has been referred to the CCMA or a bargaining council, conciliation has been attempted and failed, and 48 hours' written notice has been given.\n\nDuring a protected strike, employees may not be dismissed for participating. However, the employer is not obliged to pay wages during the strike period.\n\nAn unprotected strike (one that does not follow proper procedures) may result in dismissal or interdicts. Essential and maintenance services have additional restrictions on the right to strike.",
                legislation = listOf(
                    LegalCitation(actName = "Labour Relations Act", actNumber = "Act 66 of 1995", section = "Section 64", fullReference = "LRA s64", shortReference = "LRA s64", fullText = "64. Right to strike and recourse to lock-out.—(1) Every employee has the right to strike and every employer has recourse to lock-out if—(a) the issue in dispute has been referred to a council or to the Commission as required by this Act, and—(i) a certificate stating that the dispute remains unresolved has been issued; or (ii) a period of 30 days, or any extension of that period agreed to between the parties to the dispute, has elapsed since the referral was received by the council or the Commission; and (b) in the case of a proposed strike, at least 48 hours' notice of the commencement of the strike, in writing, has been given to the employer.")
                ),
                fullLegalText = "Section 64 of the LRA sets out the requirements for a protected strike: referral to CCMA/council, failed conciliation, and 48 hours' written notice. Protected strikers may not be dismissed for striking.",
                relatedTopicIds = listOf("wp_ccma", "wp_unfair_dismissal")
            )
        )
    )

    // ── Consumer Protection (6 topics) ───────────────────────────────────

    private fun buildConsumerProtection(): RightsCategory = RightsCategory(
        id = "consumer",
        name = "Consumer Protection",
        description = "Your rights when buying goods and services",
        iconName = "shopping_cart",
        topics = listOf(
            RightsTopic(
                id = "cp_returns",
                title = "Right to Return Goods",
                summary = "Under the Consumer Protection Act, you have the right to return goods within six months if they are defective, not suitable for their intended purpose, or not of good quality. The supplier must repair, replace, or refund the goods at your choice.\n\nFor direct marketing purchases (online, phone, or door-to-door sales), you have a cooling-off period of five business days during which you can cancel without penalty.\n\nRetailers cannot refuse a return for defective goods by pointing to a 'no refund' policy — such policies are unlawful under the CPA.",
                legislation = listOf(
                    LegalCitation(actName = "Consumer Protection Act", actNumber = "Act 68 of 2008", section = "Section 56", fullReference = "CPA s56", shortReference = "CPA s56", fullText = "56. Implied warranty of quality.—(1) In any transaction or agreement pertaining to the supply of goods to a consumer, there is an implied provision that the producer or importer, the distributor and the retailer each warrant that the goods comply with the requirements and standards contemplated in section 55.")
                ),
                fullLegalText = "Section 56 of the CPA provides an implied warranty of quality. Consumers may return defective goods within six months for repair, replacement, or refund.",
                relatedTopicIds = listOf("cp_warranties", "cp_unfair_practices")
            ),
            RightsTopic(
                id = "cp_warranties",
                title = "Product Warranties",
                summary = "The CPA provides an automatic implied warranty on all goods sold. This warranty lasts for six months from the date of purchase and covers defects in quality, performance, and durability.\n\nThis implied warranty exists in addition to any manufacturer's warranty. A retailer cannot limit or exclude this warranty through their terms and conditions.\n\nIf goods fail within six months, the defect is presumed to have existed at the time of purchase unless the supplier can prove otherwise.",
                legislation = listOf(
                    LegalCitation(actName = "Consumer Protection Act", actNumber = "Act 68 of 2008", section = "Section 55", fullReference = "CPA s55", shortReference = "CPA s55", fullText = "55. Consumer's rights to safe, good quality goods.—(1) Every consumer has a right to receive goods that—(a) are reasonably suitable for the purposes for which they are generally intended; (b) are of good quality, in good working order and free of any defects...")
                ),
                fullLegalText = "Section 55 establishes the consumer's right to goods that are safe, of good quality, and suitable for their intended purpose.",
                relatedTopicIds = listOf("cp_returns")
            ),
            RightsTopic(
                id = "cp_unfair_practices",
                title = "Unfair Business Practices",
                summary = "The CPA prohibits a wide range of unfair business practices including misleading advertising, bait marketing, negative option marketing, and unconscionable conduct.\n\nA supplier may not use physical force, coercion, undue influence, pressure, or harassment when marketing goods or services. False or misleading representations about goods are prohibited.\n\nConsumers can lodge complaints with the National Consumer Commission or the relevant industry ombud.",
                legislation = listOf(
                    LegalCitation(actName = "Consumer Protection Act", actNumber = "Act 68 of 2008", section = "Section 40", fullReference = "CPA s40", shortReference = "CPA s40", fullText = "40. Unconscionable conduct.—(1) A supplier or an agent of the supplier must not use physical force against a consumer, coercion, undue influence, pressure, duress or harassment, unfair tactics or any other similar conduct, in connection with any—(a) marketing of any goods or services...")
                ),
                fullLegalText = "Section 40 of the CPA prohibits unconscionable conduct by suppliers in marketing, supply, or negotiation of goods and services.",
                relatedTopicIds = listOf("cp_returns", "cp_warranties")
            )
        )
    )

    // ── Housing & Property (5 topics) ────────────────────────────────────

    private fun buildHousingProperty(): RightsCategory = RightsCategory(
        id = "housing",
        name = "Housing & Property",
        description = "Tenant, landlord, and property rights",
        iconName = "home",
        topics = listOf(
            RightsTopic(
                id = "hp_tenant_rights",
                title = "Tenant Rights",
                summary = "As a tenant, you have the right to a written lease agreement, a habitable dwelling, and protection against unfair practices by your landlord. The Rental Housing Act protects tenants from exploitation.\n\nYour landlord must maintain the property in a habitable condition and may not enter the premises without reasonable notice. Rent increases must be reasonable and in line with the lease agreement.\n\nDisputes between tenants and landlords can be referred to the Rental Housing Tribunal in your province, which provides free dispute resolution.",
                legislation = listOf(
                    LegalCitation(actName = "Rental Housing Act", actNumber = "Act 50 of 1999", section = "Section 4", fullReference = "RHA s4", shortReference = "RHA s4", fullText = "4. Rights of tenants and landlords.—(1) A tenant's rights include the right to have the dwelling maintained in a condition that is suitable for the purpose for which it was let...")
                ),
                fullLegalText = "The Rental Housing Act sets out the rights and obligations of both tenants and landlords, including maintenance obligations, deposit handling, and dispute resolution through the Rental Housing Tribunal.",
                relatedTopicIds = listOf("hp_eviction", "hp_deposits")
            ),
            RightsTopic(
                id = "hp_eviction",
                title = "Protection Against Eviction",
                summary = "No person may be evicted from their home without a court order. The Prevention of Illegal Eviction Act (PIE) requires that evictions follow a specific legal process.\n\nThe court must consider all relevant circumstances, including the rights of the elderly, children, disabled persons, and female-headed households. The availability of alternative accommodation is also considered.\n\nAn eviction without a court order is unlawful, regardless of whether the occupier has a lease or not.",
                legislation = listOf(
                    LegalCitation(actName = "Prevention of Illegal Eviction Act", actNumber = "Act 19 of 1998", section = "Section 4", fullReference = "PIE s4", shortReference = "PIE s4", fullText = "4. Eviction of unlawful occupiers.—(1) Notwithstanding anything to the contrary contained in any law or the common law, the provisions of this section apply to proceedings by an owner or person in charge of land for the eviction of an unlawful occupier.")
                ),
                fullLegalText = "PIE Act Section 4 requires a court order for all evictions and mandates consideration of the circumstances of the occupier, including vulnerability factors.",
                relatedTopicIds = listOf("hp_tenant_rights")
            ),
            RightsTopic(
                id = "hp_deposits",
                title = "Rental Deposits",
                summary = "A landlord may require a deposit before the tenant moves in. This deposit must be held in an interest-bearing account, and the interest accrues to the tenant.\n\nWithin seven days of the tenant vacating, the landlord must inspect the property and provide a list of any deductions. The balance of the deposit plus interest must be refunded within 14 days of the inspection.\n\nA landlord may not use the deposit for normal wear and tear — only for actual damage caused by the tenant.",
                legislation = listOf(
                    LegalCitation(actName = "Rental Housing Act", actNumber = "Act 50 of 1999", section = "Section 5", fullReference = "RHA s5", shortReference = "RHA s5", fullText = "5. Deposit.—(1) A landlord may require a tenant to pay a deposit, which must be invested in an interest-bearing account with a financial institution...")
                ),
                fullLegalText = "Section 5 of the Rental Housing Act regulates deposits, requiring interest-bearing accounts and timely refunds after inspection.",
                relatedTopicIds = listOf("hp_tenant_rights")
            )
        )
    )

    // ── Constitutional Rights (27 topics — showing 3 for MVP) ────────────

    private fun buildConstitutionalRights(): RightsCategory = RightsCategory(
        id = "constitutional",
        name = "Constitutional Rights",
        description = "The Bill of Rights — Chapter 2 of the Constitution",
        iconName = "account_balance",
        topics = listOf(
            RightsTopic(
                id = "cr_equality",
                title = "Right to Equality",
                summary = "Everyone is equal before the law and has the right to equal protection and benefit of the law. The state may not unfairly discriminate directly or indirectly against anyone on grounds including race, gender, sex, pregnancy, marital status, ethnic or social origin, colour, sexual orientation, age, disability, religion, conscience, belief, culture, language, and birth.\n\nThe Equality Act (PEPUDA) gives effect to this right and allows anyone to bring a complaint of unfair discrimination to the Equality Court.\n\nAffirmative action measures designed to protect or advance persons disadvantaged by unfair discrimination are permitted under the Constitution.",
                legislation = listOf(
                    LegalCitation(actName = "Constitution of South Africa", actNumber = "Act 108 of 1996", section = "Section 9", fullReference = "Constitution s9", shortReference = "s9", fullText = "9. Equality.—(1) Everyone is equal before the law and has the right to equal protection and benefit of the law. (2) Equality includes the full and equal enjoyment of all rights and freedoms. To promote the achievement of equality, legislative and other measures designed to protect or advance persons, or categories of persons, disadvantaged by unfair discrimination may be taken.")
                ),
                fullLegalText = "Section 9 of the Constitution guarantees equality before the law and prohibits unfair discrimination on listed grounds. It permits affirmative action measures.",
                relatedTopicIds = listOf("cr_dignity", "cr_freedom_expression")
            ),
            RightsTopic(
                id = "cr_dignity",
                title = "Right to Human Dignity",
                summary = "Everyone has inherent dignity and the right to have their dignity respected and protected. This is one of the foundational values of the Constitution and underpins all other rights.\n\nThe right to dignity has been used by courts to protect against degrading treatment, to uphold privacy, and to ensure that laws treat people as worthy of respect.\n\nThis right cannot be limited — it is considered absolute in South African constitutional law.",
                legislation = listOf(
                    LegalCitation(actName = "Constitution of South Africa", actNumber = "Act 108 of 1996", section = "Section 10", fullReference = "Constitution s10", shortReference = "s10", fullText = "10. Human dignity.—Everyone has inherent dignity and the right to have their dignity respected and protected.")
                ),
                fullLegalText = "Section 10 of the Constitution protects the inherent dignity of every person. This right is foundational and informs the interpretation of all other rights.",
                relatedTopicIds = listOf("cr_equality")
            ),
            RightsTopic(
                id = "cr_freedom_expression",
                title = "Freedom of Expression",
                summary = "Everyone has the right to freedom of expression, which includes freedom of the press and other media, freedom to receive or impart information or ideas, freedom of artistic creativity, and academic freedom.\n\nThis right does not extend to propaganda for war, incitement of imminent violence, or advocacy of hatred based on race, ethnicity, gender, or religion that constitutes incitement to cause harm.\n\nThe right to freedom of expression must be balanced against other rights, including the right to dignity and privacy.",
                legislation = listOf(
                    LegalCitation(actName = "Constitution of South Africa", actNumber = "Act 108 of 1996", section = "Section 16", fullReference = "Constitution s16", shortReference = "s16", fullText = "16. Freedom of expression.—(1) Everyone has the right to freedom of expression, which includes—(a) freedom of the press and other media; (b) freedom to receive or impart information or ideas; (c) freedom of artistic creativity; and (d) academic freedom and freedom of scientific research.")
                ),
                fullLegalText = "Section 16 guarantees freedom of expression but excludes propaganda for war, incitement to violence, and hate speech.",
                relatedTopicIds = listOf("cr_equality", "cr_dignity")
            )
        )
    )

    // ── Criminal Law (7 topics — showing 3) ──────────────────────────────

    private fun buildCriminalLaw(): RightsCategory = RightsCategory(
        id = "criminal",
        name = "Criminal Law",
        description = "Your rights when dealing with the criminal justice system",
        iconName = "shield",
        topics = listOf(
            RightsTopic(
                id = "cl_arrest_rights",
                title = "Rights Upon Arrest",
                summary = "When you are arrested, you have the right to remain silent and to be informed of this right. You have the right to be informed of the reason for your arrest. You must be brought before a court within 48 hours.\n\nYou have the right to legal representation, and if you cannot afford a lawyer, one must be provided by the state at state expense. You may not be compelled to make a confession.\n\nThe police must treat you with dignity and may not use excessive force during arrest.",
                legislation = listOf(
                    LegalCitation(actName = "Constitution of South Africa", actNumber = "Act 108 of 1996", section = "Section 35", fullReference = "Constitution s35", shortReference = "s35", fullText = "35. Arrested, detained and accused persons.—(1) Everyone who is arrested for allegedly committing an offence has the right—(a) to remain silent; (b) to be informed promptly—(i) of the right to remain silent; and (ii) of the consequences of not remaining silent; (c) not to be compelled to make any confession or admission that could be used in evidence against that person...")
                ),
                fullLegalText = "Section 35 of the Constitution sets out comprehensive rights for arrested, detained, and accused persons, including the right to silence, legal representation, and a fair trial.",
                relatedTopicIds = listOf("cl_bail", "cl_search_seizure")
            ),
            RightsTopic(
                id = "cl_bail",
                title = "Right to Bail",
                summary = "Every accused person has the right to be released on bail unless the interests of justice require otherwise. The court considers factors such as the seriousness of the offence, the likelihood of the accused fleeing, and whether the accused poses a danger to the community.\n\nFor Schedule 5 and 6 offences (serious crimes like murder, rape, and robbery with aggravating circumstances), the burden shifts to the accused to show exceptional circumstances why bail should be granted.\n\nBail conditions may include reporting to a police station, surrendering your passport, or paying a bail amount.",
                legislation = listOf(
                    LegalCitation(actName = "Criminal Procedure Act", actNumber = "Act 51 of 1977", section = "Section 60", fullReference = "CPA-Crim s60", shortReference = "CPA s60", fullText = "60. Bail application.—(1) An accused who is in custody in respect of an offence shall, subject to the provisions of section 50(6), be entitled to be released on bail at any stage preceding his or her conviction in respect of such offence...")
                ),
                fullLegalText = "Section 60 of the Criminal Procedure Act governs bail applications, setting out the factors courts must consider and the different schedules of offences.",
                relatedTopicIds = listOf("cl_arrest_rights")
            ),
            RightsTopic(
                id = "cl_search_seizure",
                title = "Search and Seizure",
                summary = "The police generally need a search warrant to search your home or property. A warrant must be issued by a magistrate or judge and must specify what is being searched for and where.\n\nThere are exceptions: police may search without a warrant if they have reasonable grounds to believe that a warrant would be issued but the delay in obtaining it would defeat the purpose of the search.\n\nAnything seized unlawfully may be excluded as evidence in court. You have the right to challenge the legality of a search.",
                legislation = listOf(
                    LegalCitation(actName = "Criminal Procedure Act", actNumber = "Act 51 of 1977", section = "Section 21", fullReference = "CPA-Crim s21", shortReference = "CPA s21", fullText = "21. Article to be seized under search warrant.—(1) Subject to the provisions of sections 22, 24 and 25, an article referred to in section 20 shall be seized only by virtue of a search warrant issued—(a) by a magistrate or justice, if it appears to such magistrate or justice from information on oath that there are reasonable grounds for believing that any such article is in the possession or under the control of or upon any person or upon or at any premises within his or her area of jurisdiction...")
                ),
                fullLegalText = "Section 21 of the Criminal Procedure Act requires search warrants for seizure of articles, with exceptions for urgent circumstances under Section 22.",
                relatedTopicIds = listOf("cl_arrest_rights")
            )
        )
    )

    // ── Family Law (6 topics — showing 3) ────────────────────────────────

    private fun buildFamilyLaw(): RightsCategory = RightsCategory(
        id = "family",
        name = "Family Law",
        description = "Marriage, divorce, maintenance, and children's rights",
        iconName = "family_restroom",
        topics = listOf(
            RightsTopic(
                id = "fl_maintenance",
                title = "Maintenance & Child Support",
                summary = "Both parents have a legal duty to maintain their children, regardless of whether they are married or not. The Maintenance Act provides a mechanism to claim maintenance through the Maintenance Court.\n\nTo apply for maintenance, you can approach the maintenance officer at your nearest magistrate's court. The process is free. The court will consider the needs of the child and the financial means of both parents.\n\nFailure to pay maintenance is a criminal offence. The court can issue a warrant of execution against the defaulter's property or attach their salary.",
                legislation = listOf(
                    LegalCitation(actName = "Maintenance Act", actNumber = "Act 99 of 1998", section = "Section 15", fullReference = "Maintenance Act s15", shortReference = "MA s15", fullText = "15. Maintenance order.—(1) The maintenance court may make an order against any person legally liable to maintain any other person—(a) for the payment of maintenance to the person so entitled; (b) for the payment of a lump sum...")
                ),
                fullLegalText = "The Maintenance Act provides for the enforcement of maintenance obligations through the Maintenance Court, including salary attachments and criminal sanctions for non-payment.",
                relatedTopicIds = listOf("fl_domestic_violence", "fl_childrens_rights")
            ),
            RightsTopic(
                id = "fl_domestic_violence",
                title = "Domestic Violence Protection",
                summary = "The Domestic Violence Act provides protection against domestic violence, which includes physical, sexual, emotional, verbal, psychological, and economic abuse. It also covers intimidation, harassment, stalking, and damage to property.\n\nAny person in a domestic relationship can apply for a protection order at the magistrate's court. The application is free and can be made without a lawyer.\n\nA protection order can prohibit the abuser from committing further acts of violence, entering the shared home, or contacting the complainant. Breach of a protection order is a criminal offence.",
                legislation = listOf(
                    LegalCitation(actName = "Domestic Violence Act", actNumber = "Act 116 of 1998", section = "Section 4", fullReference = "DVA s4", shortReference = "DVA s4", fullText = "4. Application for protection order.—(1) Any complainant may in the prescribed manner apply to the court for a protection order. (2) If the court is satisfied that there is prima facie evidence that—(a) the respondent is committing, or has committed an act of domestic violence; and (b) undue hardship may be suffered by the complainant as a result of such domestic violence if a protection order is not issued, the court must issue an interim protection order against the respondent.")
                ),
                fullLegalText = "The Domestic Violence Act provides for protection orders against domestic violence, broadly defined to include physical, emotional, economic, and other forms of abuse.",
                relatedTopicIds = listOf("fl_maintenance")
            ),
            RightsTopic(
                id = "fl_childrens_rights",
                title = "Children's Rights",
                summary = "The Children's Act provides comprehensive protection for children's rights. Every child has the right to family care or parental care, or appropriate alternative care. The best interests of the child are paramount in all matters concerning the child.\n\nParental responsibilities and rights include caring for the child, maintaining contact, acting as guardian, and contributing to the child's maintenance. These responsibilities apply to both parents equally.\n\nThe Children's Court can make orders regarding the care, contact, and guardianship of children when parents cannot agree.",
                legislation = listOf(
                    LegalCitation(actName = "Children's Act", actNumber = "Act 38 of 2005", section = "Section 7", fullReference = "Children's Act s7", shortReference = "CA s7", fullText = "7. Best interests of child standard.—(1) Whenever a provision of this Act requires the best interests of the child standard to be applied, the following factors must be taken into consideration where relevant...")
                ),
                fullLegalText = "The Children's Act establishes the best interests of the child as the paramount consideration and sets out parental responsibilities and rights.",
                relatedTopicIds = listOf("fl_maintenance", "fl_domestic_violence")
            )
        )
    )

    // ── Privacy & Data (4 topics — showing 3) ────────────────────────────

    private fun buildPrivacyData(): RightsCategory = RightsCategory(
        id = "privacy",
        name = "Privacy & Data",
        description = "Your rights under POPIA and data protection law",
        iconName = "lock",
        topics = listOf(
            RightsTopic(
                id = "pd_popia_rights",
                title = "Your POPIA Rights",
                summary = "The Protection of Personal Information Act (POPIA) gives you the right to know what personal information is being collected about you, why it is being collected, and how it will be used.\n\nYou have the right to access your personal information held by any organisation, to request correction of inaccurate information, and to request deletion of your information in certain circumstances.\n\nOrganisations must obtain your consent before processing your personal information, and they must take reasonable measures to protect it from unauthorised access.",
                legislation = listOf(
                    LegalCitation(actName = "Protection of Personal Information Act", actNumber = "Act 4 of 2013", section = "Section 5", fullReference = "POPIA s5", shortReference = "POPIA s5", fullText = "5. Rights of data subjects.—A data subject has the right to have his, her or its personal information processed in accordance with the conditions for the lawful processing of personal information as referred to in Chapter 3...")
                ),
                fullLegalText = "POPIA Section 5 establishes the rights of data subjects, including the right to access, correct, and delete personal information.",
                relatedTopicIds = listOf("pd_data_breach", "pd_consent")
            ),
            RightsTopic(
                id = "pd_data_breach",
                title = "Data Breach Notification",
                summary = "If an organisation suffers a data breach that compromises your personal information, they are legally required to notify both you and the Information Regulator as soon as reasonably possible.\n\nThe notification must include the nature of the breach, what information was compromised, what steps the organisation is taking, and what you can do to protect yourself.\n\nFailure to notify is an offence under POPIA and can result in significant fines.",
                legislation = listOf(
                    LegalCitation(actName = "Protection of Personal Information Act", actNumber = "Act 4 of 2013", section = "Section 22", fullReference = "POPIA s22", shortReference = "POPIA s22", fullText = "22. Notification of security compromises.—(1) Where there are reasonable grounds to believe that the personal information of a data subject has been accessed or acquired by any unauthorised person, the responsible party must notify—(a) the Regulator; and (b) subject to subsection (3), the data subject, unless the identity of such data subject cannot be established...")
                ),
                fullLegalText = "POPIA Section 22 requires responsible parties to notify the Information Regulator and affected data subjects of security compromises.",
                relatedTopicIds = listOf("pd_popia_rights")
            ),
            RightsTopic(
                id = "pd_consent",
                title = "Consent & Direct Marketing",
                summary = "Under POPIA, organisations generally need your consent to process your personal information. Consent must be voluntary, specific, and informed — you must know what you are agreeing to.\n\nFor direct marketing (emails, SMS, phone calls), you must give explicit consent before an organisation can contact you. You have the right to opt out of direct marketing at any time.\n\nThe Consumer Protection Act also regulates direct marketing, giving you the right to pre-emptively block all direct marketing by registering on the National Opt-Out Registry.",
                legislation = listOf(
                    LegalCitation(actName = "Protection of Personal Information Act", actNumber = "Act 4 of 2013", section = "Section 69", fullReference = "POPIA s69", shortReference = "POPIA s69", fullText = "69. Direct marketing by means of unsolicited electronic communications.—(1) The processing of personal information of a data subject for the purpose of direct marketing by means of any form of electronic communication, including automatic calling machines, facsimile machines, SMSs or e-mail is prohibited unless the data subject—(a) has given his, her or its consent to the processing; or (b) is, subject to subsection (3), an existing customer of the responsible party.")
                ),
                fullLegalText = "POPIA Section 69 regulates direct marketing via electronic communications, requiring prior consent except for existing customers under certain conditions.",
                relatedTopicIds = listOf("pd_popia_rights")
            )
        )
    )

    // ── Road & Traffic (5 topics — showing 3) ────────────────────────────

    private fun buildRoadTraffic(): RightsCategory = RightsCategory(
        id = "traffic",
        name = "Road & Traffic",
        description = "Your rights on the road and traffic law",
        iconName = "directions_car",
        topics = listOf(
            RightsTopic(
                id = "rt_traffic_fines",
                title = "Traffic Fines & AARTO",
                summary = "The Administrative Adjudication of Road Traffic Offences Act (AARTO) governs how traffic fines are issued and enforced. You have the right to challenge any traffic fine you believe is incorrect.\n\nYou can make representations (challenge the fine) within 32 days of receiving the infringement notice. If your representations are rejected, you can elect to be tried in court.\n\nUnder AARTO's demerit system, points are assigned for traffic offences. Accumulating too many points can result in suspension of your driving licence.",
                legislation = listOf(
                    LegalCitation(actName = "Administrative Adjudication of Road Traffic Offences Act", actNumber = "Act 46 of 1998", section = "Section 17", fullReference = "AARTO s17", shortReference = "AARTO s17", fullText = "17. Representations.—(1) An infringer who wishes to make representations in respect of an infringement notice must, within 32 days of the date of the infringement notice, deliver to the issuing authority written representations...")
                ),
                fullLegalText = "AARTO Section 17 provides the right to make representations against traffic infringement notices within 32 days.",
                relatedTopicIds = listOf("rt_roadblocks", "rt_accidents")
            ),
            RightsTopic(
                id = "rt_roadblocks",
                title = "Rights at Roadblocks",
                summary = "Police and traffic officers may set up roadblocks, but they must follow specific procedures. Officers must identify themselves and explain the purpose of the roadblock.\n\nYou are required to stop at a lawful roadblock and produce your driver's licence, vehicle registration, and proof of insurance (if applicable). However, you are not required to answer questions beyond identifying yourself.\n\nOfficers may search your vehicle at a roadblock only if they have reasonable grounds to suspect that an offence has been committed or that evidence of an offence will be found.",
                legislation = listOf(
                    LegalCitation(actName = "National Road Traffic Act", actNumber = "Act 93 of 1996", section = "Section 3I", fullReference = "NRTA s3I", shortReference = "NRTA s3I", fullText = "3I. Stopping of vehicles.—(1) Any traffic officer may at any time require the driver of a vehicle on a public road to stop such vehicle...")
                ),
                fullLegalText = "The NRTA authorises traffic officers to stop vehicles and request documentation. Searches require reasonable suspicion under the Criminal Procedure Act.",
                relatedTopicIds = listOf("rt_traffic_fines")
            ),
            RightsTopic(
                id = "rt_accidents",
                title = "Accident Procedures",
                summary = "If you are involved in a road accident, you are legally required to stop immediately. If anyone is injured, you must report the accident to the nearest police station within 24 hours.\n\nYou must provide your name, address, and vehicle registration number to any person who has reasonable grounds to request it. Failing to stop after an accident (hit and run) is a serious criminal offence.\n\nThe Road Accident Fund (RAF) provides compensation for injuries sustained in road accidents, regardless of who was at fault.",
                legislation = listOf(
                    LegalCitation(actName = "National Road Traffic Act", actNumber = "Act 93 of 1996", section = "Section 61", fullReference = "NRTA s61", shortReference = "NRTA s61", fullText = "61. Duty of driver in event of accident.—(1) The driver of a vehicle on a public road at the time when such vehicle is involved in or contributes to any accident in which any other person is killed or injured or suffers damage in respect of any property or animal, shall—(a) immediately stop the vehicle...")
                ),
                fullLegalText = "NRTA Section 61 sets out the duties of drivers involved in accidents, including the obligation to stop, provide information, and report to police.",
                relatedTopicIds = listOf("rt_traffic_fines", "rt_roadblocks")
            )
        )
    )
}
