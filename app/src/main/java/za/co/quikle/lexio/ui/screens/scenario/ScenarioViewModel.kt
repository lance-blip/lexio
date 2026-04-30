package za.co.quikle.lexio.ui.screens.scenario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import za.co.quikle.lexio.domain.model.ApplicableLaw
import za.co.quikle.lexio.domain.model.NextStep
import za.co.quikle.lexio.domain.model.ScenarioAnalysis

enum class ScenarioStep { INPUT, LOADING, RESULTS }

data class ScenarioUiState(
    val currentStep: ScenarioStep = ScenarioStep.INPUT,
    val scenarioInput: String = "",
    val whoInvolved: String = "",
    val whatHappened: String = "",
    val whenHappened: String = "",
    val whereHappened: String = "",
    val isGuidedExpanded: Boolean = false,
    val loadingProgress: Int = 0,
    val analysisResult: ScenarioAnalysis? = null,
    val identifiedCategory: String = ""
)

class ScenarioViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ScenarioUiState())
    val uiState: StateFlow<ScenarioUiState> = _uiState.asStateFlow()

    fun onInputChanged(input: String) {
        _uiState.value = _uiState.value.copy(scenarioInput = input)
    }

    fun onWhoChanged(value: String) {
        _uiState.value = _uiState.value.copy(whoInvolved = value)
    }

    fun onWhatChanged(value: String) {
        _uiState.value = _uiState.value.copy(whatHappened = value)
    }

    fun onWhenChanged(value: String) {
        _uiState.value = _uiState.value.copy(whenHappened = value)
    }

    fun onWhereChanged(value: String) {
        _uiState.value = _uiState.value.copy(whereHappened = value)
    }

    fun toggleGuidedPrompts() {
        _uiState.value = _uiState.value.copy(isGuidedExpanded = !_uiState.value.isGuidedExpanded)
    }

    fun useGuidedDetails() {
        val state = _uiState.value
        val parts = mutableListOf<String>()
        if (state.whoInvolved.isNotBlank()) parts.add("Who was involved: ${state.whoInvolved}")
        if (state.whatHappened.isNotBlank()) parts.add("What happened: ${state.whatHappened}")
        if (state.whenHappened.isNotBlank()) parts.add("When it happened: ${state.whenHappened}")
        if (state.whereHappened.isNotBlank()) parts.add("Where it happened: ${state.whereHappened}")

        val combined = if (state.scenarioInput.isNotBlank()) {
            state.scenarioInput + "\n\n" + parts.joinToString("\n")
        } else {
            parts.joinToString("\n")
        }

        _uiState.value = state.copy(
            scenarioInput = combined,
            isGuidedExpanded = false,
            whoInvolved = "",
            whatHappened = "",
            whenHappened = "",
            whereHappened = ""
        )
    }

    fun analyseScenario() {
        val text = _uiState.value.scenarioInput.trim()
        if (text.length < 20) return

        _uiState.value = _uiState.value.copy(
            currentStep = ScenarioStep.LOADING,
            loadingProgress = 0
        )

        viewModelScope.launch {
            // Simulate loading progress
            for (i in 1..4) {
                delay(750L)
                _uiState.value = _uiState.value.copy(loadingProgress = i)
            }

            val analysis = generateMockAnalysis(text)
            _uiState.value = _uiState.value.copy(
                currentStep = ScenarioStep.RESULTS,
                analysisResult = analysis
            )
        }
    }

    fun resetToInput() {
        _uiState.value = ScenarioUiState()
    }

    private fun generateMockAnalysis(text: String): ScenarioAnalysis {
        val lowerText = text.lowercase()

        return when {
            lowerText.contains("fire") || lowerText.contains("dismiss") || lowerText.contains("employer") -> {
                _uiState.value = _uiState.value.copy(identifiedCategory = "Labour Law")
                ScenarioAnalysis(
                    userScenario = text,
                    applicableLaws = listOf(
                        ApplicableLaw(
                            actName = "Labour Relations Act (Act 66 of 1995)",
                            sections = listOf("Section 188 — Unfair Dismissal", "Section 189 — Dismissal for Operational Requirements", "Section 191 — Referral to CCMA"),
                            relevance = "This Act governs the employment relationship and sets out the requirements for fair dismissal. Section 188 requires that every dismissal must be both substantively and procedurally fair."
                        ),
                        ApplicableLaw(
                            actName = "Basic Conditions of Employment Act (Act 75 of 1997)",
                            sections = listOf("Section 37 — Notice of Termination", "Section 40 — Severance Pay"),
                            relevance = "Sets minimum notice periods and severance pay requirements for employees who are dismissed for operational reasons."
                        )
                    ),
                    analysis = "Based on your description, this situation falls under South African labour law. The Labour Relations Act requires that any dismissal must meet two tests: it must be substantively fair (there must be a valid reason such as misconduct, incapacity, or operational requirements) and procedurally fair (the employer must follow a fair process, including giving the employee a hearing).\n\nIf the employer failed to follow a fair procedure — such as not giving a hearing, not allowing representation, or not providing adequate notice of the charges — the dismissal may be deemed procedurally unfair regardless of whether there was a valid reason for it.\n\nThe employee has 30 days from the date of dismissal to refer the matter to the CCMA for conciliation.",
                    potentialViolations = listOf(
                        "Failure to conduct a disciplinary hearing before dismissal (procedural unfairness)",
                        "Possible lack of valid reason for dismissal (substantive unfairness)",
                        "Failure to provide adequate notice as required by BCEA Section 37"
                    ),
                    rights = listOf(
                        "Right to a fair hearing before dismissal",
                        "Right to be represented by a fellow employee or trade union representative",
                        "Right to be informed of the reasons for dismissal",
                        "Right to refer the dispute to the CCMA within 30 days",
                        "Right to reinstatement or compensation if dismissal is found unfair"
                    ),
                    nextSteps = listOf(
                        NextStep(
                            action = "File a case with the CCMA",
                            description = "Refer the unfair dismissal dispute to the Commission for Conciliation, Mediation and Arbitration within 30 days of dismissal.",
                            contactInfo = "CCMA: 0861 16 16 16",
                            url = "https://www.ccma.org.za"
                        ),
                        NextStep(
                            action = "Consult a labour attorney",
                            description = "Get professional legal advice about the strength of your case and the best course of action.",
                            contactInfo = null,
                            url = null
                        ),
                        NextStep(
                            action = "Contact Legal Aid SA",
                            description = "If you cannot afford a private attorney, Legal Aid South Africa provides free legal assistance to qualifying individuals.",
                            contactInfo = "Legal Aid SA: 0800 110 110",
                            url = "https://www.legal-aid.co.za"
                        )
                    )
                )
            }

            lowerText.contains("landlord") || lowerText.contains("rent") || lowerText.contains("evict") || lowerText.contains("tenant") -> {
                _uiState.value = _uiState.value.copy(identifiedCategory = "Housing Law")
                ScenarioAnalysis(
                    userScenario = text,
                    applicableLaws = listOf(
                        ApplicableLaw(
                            actName = "Rental Housing Act (Act 50 of 1999)",
                            sections = listOf("Section 4 — Rights of Tenants", "Section 5 — Obligations of Landlords", "Section 13 — Rental Housing Tribunal"),
                            relevance = "Governs the relationship between landlords and tenants, including rights regarding deposits, maintenance, and dispute resolution."
                        ),
                        ApplicableLaw(
                            actName = "Prevention of Illegal Eviction Act (Act 19 of 1998)",
                            sections = listOf("Section 4 — Eviction of Unlawful Occupiers", "Section 6 — Urgent Proceedings"),
                            relevance = "Prohibits illegal eviction and requires a court order for any eviction. The court must consider all relevant circumstances including the rights of the elderly, children, and disabled persons."
                        )
                    ),
                    analysis = "Your situation involves housing and tenant rights under South African law. The Rental Housing Act provides significant protections for tenants, including the right to have the property maintained in a habitable condition and the right to have deposits held in interest-bearing accounts.\n\nImportantly, no landlord may evict a tenant without first obtaining a court order. The Prevention of Illegal Eviction Act (PIE) makes it a criminal offence to evict someone without following the proper legal process. Even if a lease has expired, the landlord must still approach the court for an eviction order.\n\nIf you have a dispute with your landlord, you can approach the Rental Housing Tribunal in your province for free mediation and dispute resolution.",
                    potentialViolations = listOf(
                        "Possible illegal eviction without a court order (PIE Act violation)",
                        "Failure to maintain the property in a habitable condition"
                    ),
                    rights = listOf(
                        "Right not to be evicted without a court order",
                        "Right to have your deposit held in an interest-bearing account",
                        "Right to have the property maintained in a habitable condition",
                        "Right to approach the Rental Housing Tribunal for dispute resolution"
                    ),
                    nextSteps = listOf(
                        NextStep(
                            action = "Contact the Rental Housing Tribunal",
                            description = "File a complaint with the Rental Housing Tribunal in your province for free dispute resolution.",
                            contactInfo = "Gauteng: 011 630 5035 | Western Cape: 021 483 8157",
                            url = null
                        ),
                        NextStep(
                            action = "Contact Legal Aid SA",
                            description = "Get free legal assistance if you are facing eviction or a housing dispute.",
                            contactInfo = "Legal Aid SA: 0800 110 110",
                            url = "https://www.legal-aid.co.za"
                        ),
                        NextStep(
                            action = "Document everything",
                            description = "Keep records of all communication with your landlord, photographs of the property condition, and copies of your lease agreement.",
                            contactInfo = null,
                            url = null
                        )
                    )
                )
            }

            lowerText.contains("product") || lowerText.contains("refund") || lowerText.contains("warranty") || lowerText.contains("store") || lowerText.contains("bought") -> {
                _uiState.value = _uiState.value.copy(identifiedCategory = "Consumer Protection")
                ScenarioAnalysis(
                    userScenario = text,
                    applicableLaws = listOf(
                        ApplicableLaw(
                            actName = "Consumer Protection Act (Act 68 of 2008)",
                            sections = listOf("Section 56 — Implied Warranty of Quality", "Section 55 — Consumer's Right to Safe, Good Quality Goods", "Section 54 — Consumer's Right to Demand Quality Service"),
                            relevance = "Provides comprehensive consumer rights including the right to return defective goods within 6 months and the right to fair, honest dealing."
                        ),
                        ApplicableLaw(
                            actName = "National Credit Act (Act 34 of 2005)",
                            sections = listOf("Section 129 — Required Procedures Before Debt Enforcement"),
                            relevance = "If the purchase involved credit, the NCA provides additional protections regarding debt collection and enforcement."
                        )
                    ),
                    analysis = "This situation falls under consumer protection law. The Consumer Protection Act gives you strong rights when purchasing goods and services in South Africa.\n\nUnder Section 56, if goods are defective, unsafe, or not fit for their intended purpose, you have the right to return them within 6 months of purchase and choose between a full refund, replacement, or repair. This is your choice — the seller cannot force you to accept a repair if you want a refund.\n\nThe CPA also protects you against misleading marketing, unfair contract terms, and the right to cancel direct marketing purchases within 5 business days.",
                    potentialViolations = listOf(
                        "Possible sale of defective goods (CPA Section 56 violation)",
                        "Possible refusal to honour the implied warranty of quality",
                        "Possible unfair or misleading business practices"
                    ),
                    rights = listOf(
                        "Right to return defective goods within 6 months for refund, repair, or replacement",
                        "Right to safe, good quality goods",
                        "Right to fair and honest dealing",
                        "Right to lodge a complaint with the National Consumer Commission"
                    ),
                    nextSteps = listOf(
                        NextStep(
                            action = "Contact the National Consumer Commission",
                            description = "Lodge a formal complaint with the NCC if the business refuses to honour your consumer rights.",
                            contactInfo = "NCC: 012 428 7000",
                            url = "https://www.thencc.gov.za"
                        ),
                        NextStep(
                            action = "Write a formal complaint letter",
                            description = "Send a written complaint to the business citing the specific sections of the CPA that apply to your situation.",
                            contactInfo = null,
                            url = null
                        ),
                        NextStep(
                            action = "Contact the Consumer Goods and Services Ombud",
                            description = "For disputes involving consumer goods and services, the CGSO provides free dispute resolution.",
                            contactInfo = "CGSO: 0860 000 272",
                            url = "https://www.cgso.org.za"
                        )
                    )
                )
            }

            else -> {
                _uiState.value = _uiState.value.copy(identifiedCategory = "Constitutional Rights")
                ScenarioAnalysis(
                    userScenario = text,
                    applicableLaws = listOf(
                        ApplicableLaw(
                            actName = "Constitution of the Republic of South Africa (Act 108 of 1996)",
                            sections = listOf("Chapter 2 — Bill of Rights", "Section 9 — Equality", "Section 10 — Human Dignity", "Section 12 — Freedom and Security of the Person"),
                            relevance = "The Constitution is the supreme law of South Africa. The Bill of Rights applies to all law and binds the legislature, the executive, the judiciary, and all organs of state."
                        ),
                        ApplicableLaw(
                            actName = "Promotion of Administrative Justice Act (Act 3 of 2000)",
                            sections = listOf("Section 3 — Procedurally Fair Administrative Action", "Section 6 — Judicial Review of Administrative Action"),
                            relevance = "If the situation involves a decision by a government body or official, PAJA requires that administrative action must be lawful, reasonable, and procedurally fair."
                        )
                    ),
                    analysis = "Based on your description, this situation engages fundamental constitutional rights. The Bill of Rights in Chapter 2 of the Constitution guarantees a range of rights to every person in South Africa, including the right to equality, human dignity, freedom and security of the person, and access to courts.\n\nThese rights are not absolute — they may be limited, but only in terms of a law of general application and only to the extent that the limitation is reasonable and justifiable in an open and democratic society (Section 36 — the limitations clause).\n\nIf your rights have been violated by a government body, you may also have recourse under the Promotion of Administrative Justice Act, which requires that all administrative action be lawful, reasonable, and procedurally fair.",
                    potentialViolations = listOf(
                        "Possible infringement of constitutional rights",
                        "Possible failure to follow fair administrative procedures"
                    ),
                    rights = listOf(
                        "Right to equality before the law (Section 9)",
                        "Right to human dignity (Section 10)",
                        "Right to access courts (Section 34)",
                        "Right to just administrative action (Section 33)"
                    ),
                    nextSteps = listOf(
                        NextStep(
                            action = "Consult a legal professional",
                            description = "Constitutional matters can be complex. A qualified attorney can advise you on the best course of action.",
                            contactInfo = null,
                            url = null
                        ),
                        NextStep(
                            action = "Contact the Public Protector",
                            description = "If the situation involves government misconduct or maladministration, the Public Protector can investigate.",
                            contactInfo = "Public Protector: 0800 11 20 40",
                            url = "https://www.pprotect.org"
                        ),
                        NextStep(
                            action = "Contact the South African Human Rights Commission",
                            description = "The SAHRC can investigate complaints of human rights violations.",
                            contactInfo = "SAHRC: 011 877 3600",
                            url = "https://www.sahrc.org.za"
                        )
                    )
                )
            }
        }
    }
}
