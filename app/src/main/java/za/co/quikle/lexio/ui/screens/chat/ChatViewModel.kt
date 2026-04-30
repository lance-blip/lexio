package za.co.quikle.lexio.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import za.co.quikle.lexio.domain.model.ChatMessage
import za.co.quikle.lexio.domain.model.Confidence
import za.co.quikle.lexio.domain.model.LegalCitation

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val currentInput: String = "",
    val conversationId: String? = null,
    val prefillHandled: Boolean = false
)

class ChatViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onInputChanged(input: String) {
        _uiState.value = _uiState.value.copy(currentInput = input)
    }

    fun handlePrefill(query: String) {
        if (_uiState.value.prefillHandled) return
        _uiState.value = _uiState.value.copy(
            currentInput = query,
            prefillHandled = true
        )
        sendMessage()
    }

    fun clearConversation() {
        _uiState.value = ChatUiState()
    }

    fun sendMessage() {
        val text = _uiState.value.currentInput.trim()
        if (text.isEmpty()) return

        val userMessage = ChatMessage(
            content = text,
            isUser = true
        )

        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage,
            currentInput = "",
            isLoading = true
        )

        viewModelScope.launch {
            delay(1500L) // Simulate API call

            val response = generateMockResponse(text)

            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + response,
                isLoading = false
            )
        }
    }

    private fun generateMockResponse(query: String): ChatMessage {
        val lowerQuery = query.lowercase()

        return when {
            lowerQuery.contains("fire") || lowerQuery.contains("dismiss") || lowerQuery.contains("hearing") -> {
                ChatMessage(
                    content = "Under South African law, your employer cannot dismiss you without following a fair procedure. Section 188 of the Labour Relations Act (LRA) requires that every dismissal must be both substantively fair (there must be a valid reason) and procedurally fair (you must be given a hearing).\n\nThis means your employer must:\n• Notify you of the allegations against you\n• Give you reasonable time to prepare\n• Allow you to state your case at a disciplinary hearing\n• Allow you to be represented by a fellow employee or trade union representative\n• Consider your response before making a decision\n\nIf your employer dismissed you without following these steps, the dismissal may be automatically unfair, and you can refer the matter to the CCMA within 30 days.",
                    isUser = false,
                    citations = listOf(
                        LegalCitation(
                            actName = "Labour Relations Act",
                            actNumber = "Act 66 of 1995",
                            section = "Section 188",
                            subsection = "Subsection (1)",
                            fullReference = "LRA s188(1)",
                            shortReference = "LRA s188",
                            fullText = "A dismissal that is not in accordance with a fair procedure is unfair. A dismissal is unfair if the employer fails to prove that the reason for dismissal is a fair reason related to the employee's conduct or capacity, or is based on the employer's operational requirements."
                        ),
                        LegalCitation(
                            actName = "Labour Relations Act",
                            actNumber = "Act 66 of 1995",
                            section = "Section 191",
                            subsection = null,
                            fullReference = "LRA s191",
                            shortReference = "LRA s191",
                            fullText = "An employee who has been dismissed or has a dispute about an unfair labour practice may refer the dispute to the CCMA within 30 days of the date of dismissal."
                        )
                    ),
                    confidence = Confidence.HIGH,
                    suggestedFollowUps = listOf(
                        "How do I file a case with the CCMA?",
                        "What compensation can I get for unfair dismissal?",
                        "What is the difference between misconduct and incapacity?"
                    )
                )
            }

            lowerQuery.contains("arrest") || lowerQuery.contains("police") || lowerQuery.contains("detained") -> {
                ChatMessage(
                    content = "When you are arrested in South Africa, you have important constitutional rights that the police must respect. Section 35 of the Constitution guarantees the following rights to every arrested person:\n\n• The right to remain silent\n• The right to be informed promptly of the right to remain silent and the consequences of not remaining silent\n• The right not to be compelled to make any confession or admission\n• The right to be brought before a court within 48 hours of arrest\n• The right to be informed of the charge with sufficient detail to answer it\n• The right to legal representation, and to have a legal practitioner assigned by the state at state expense if substantial injustice would otherwise result\n\nThe police must inform you of these rights at the time of arrest. Any evidence obtained in violation of these rights may be excluded from court proceedings.",
                    isUser = false,
                    citations = listOf(
                        LegalCitation(
                            actName = "Constitution of the Republic of South Africa",
                            actNumber = "Act 108 of 1996",
                            section = "Section 35",
                            subsection = "Subsection (1)",
                            fullReference = "Constitution s35(1)",
                            shortReference = "Constitution s35",
                            fullText = "Everyone who is arrested for allegedly committing an offence has the right to remain silent; to be informed promptly of the right to remain silent, and of the consequences of not remaining silent; not to be compelled to make any confession or admission that could be used in evidence against that person; to be brought before a court as soon as reasonably possible, but not later than 48 hours after the arrest."
                        ),
                        LegalCitation(
                            actName = "Criminal Procedure Act",
                            actNumber = "Act 51 of 1977",
                            section = "Section 39",
                            subsection = null,
                            fullReference = "CPA s39",
                            shortReference = "CPA s39",
                            fullText = "An arrest shall be effected with or without a warrant. The person effecting the arrest must inform the arrested person of the cause of the arrest."
                        )
                    ),
                    confidence = Confidence.HIGH,
                    suggestedFollowUps = listOf(
                        "What happens if police don't read me my rights?",
                        "How does bail work in South Africa?",
                        "Can the police search my home without a warrant?"
                    )
                )
            }

            lowerQuery.contains("popia") || lowerQuery.contains("data") || lowerQuery.contains("privacy") || lowerQuery.contains("personal information") -> {
                ChatMessage(
                    content = "The Protection of Personal Information Act (POPIA) is South Africa's data protection law that came into full effect on 1 July 2021. It gives you significant rights over your personal information:\n\n• Right to be notified when your personal information is collected\n• Right to know what personal information an organisation holds about you\n• Right to request correction or deletion of your personal information\n• Right to object to the processing of your personal information\n• Right to complain to the Information Regulator if your rights are violated\n\nOrganisations (called \"responsible parties\") must:\n• Only collect information for a specific, lawful purpose\n• Get your consent before processing your data (in most cases)\n• Keep your information secure and confidential\n• Not keep your information longer than necessary\n\nYou can lodge a complaint with the Information Regulator if you believe your POPIA rights have been violated. Contact them at: inforeg@justice.gov.za or 012 406 4818.",
                    isUser = false,
                    citations = listOf(
                        LegalCitation(
                            actName = "Protection of Personal Information Act",
                            actNumber = "Act 4 of 2013",
                            section = "Section 5",
                            subsection = null,
                            fullReference = "POPIA s5",
                            shortReference = "POPIA s5",
                            fullText = "Personal information must be processed lawfully and in a reasonable manner that does not infringe the privacy of the data subject."
                        ),
                        LegalCitation(
                            actName = "Protection of Personal Information Act",
                            actNumber = "Act 4 of 2013",
                            section = "Section 11",
                            subsection = null,
                            fullReference = "POPIA s11",
                            shortReference = "POPIA s11",
                            fullText = "Personal information may only be processed if the data subject consents to the processing, or if processing is necessary for a legitimate purpose."
                        )
                    ),
                    confidence = Confidence.HIGH,
                    suggestedFollowUps = listOf(
                        "How do I request my data from a company?",
                        "What happens if a company has a data breach?",
                        "Can my employer monitor my emails at work?"
                    )
                )
            }

            lowerQuery.contains("landlord") || lowerQuery.contains("rent") || lowerQuery.contains("tenant") || lowerQuery.contains("evict") -> {
                ChatMessage(
                    content = "South African rental law provides important protections for tenants. Under the Rental Housing Act and the Consumer Protection Act:\n\n• Your landlord must give you reasonable written notice before increasing rent (typically at least 1 month)\n• Rent increases must be reasonable and in line with market rates\n• Your landlord cannot evict you without a court order — this is protected by the Prevention of Illegal Eviction Act (PIE)\n• Your deposit must be held in an interest-bearing account and returned within 14 days of lease termination (after deducting legitimate costs)\n• Your landlord must maintain the property in a habitable condition\n\nIf you have a dispute with your landlord, you can approach the Rental Housing Tribunal in your province for free dispute resolution.",
                    isUser = false,
                    citations = listOf(
                        LegalCitation(
                            actName = "Rental Housing Act",
                            actNumber = "Act 50 of 1999",
                            section = "Section 4",
                            subsection = null,
                            fullReference = "RHA s4",
                            shortReference = "RHA s4",
                            fullText = "A tenant has the right to have the dwelling maintained in a condition fit for habitation and to have the deposit refunded with interest upon termination of the lease."
                        )
                    ),
                    confidence = Confidence.HIGH,
                    suggestedFollowUps = listOf(
                        "How do I get my deposit back from my landlord?",
                        "Can my landlord enter my home without permission?",
                        "What is the Rental Housing Tribunal?"
                    )
                )
            }

            lowerQuery.contains("consumer") || lowerQuery.contains("refund") || lowerQuery.contains("warranty") || lowerQuery.contains("product") -> {
                ChatMessage(
                    content = "The Consumer Protection Act (CPA) gives you strong rights when buying goods and services in South Africa:\n\n• Right to return defective goods within 6 months for a full refund, repair, or replacement — your choice, not the seller's\n• Right to a cooling-off period of 5 business days for direct marketing purchases\n• Right to honest and fair dealing — businesses cannot use misleading or deceptive practices\n• Right to fair, reasonable, and just contract terms\n• Right to cancel fixed-term contracts with 20 business days' notice\n\nIf a business refuses to honour your CPA rights, you can lodge a complaint with the National Consumer Commission (NCC) at 012 428 7000.",
                    isUser = false,
                    citations = listOf(
                        LegalCitation(
                            actName = "Consumer Protection Act",
                            actNumber = "Act 68 of 2008",
                            section = "Section 56",
                            subsection = null,
                            fullReference = "CPA s56",
                            shortReference = "CPA s56",
                            fullText = "Within six months after the delivery of goods, the consumer may return the goods if they are defective, and demand a full refund, replacement, or repair."
                        )
                    ),
                    confidence = Confidence.HIGH,
                    suggestedFollowUps = listOf(
                        "Can a store refuse to give me a refund?",
                        "What is the cooling-off period for online purchases?",
                        "How do I lodge a complaint with the NCC?"
                    )
                )
            }

            else -> {
                ChatMessage(
                    content = "Thank you for your question. South African law covers a wide range of topics, and I'm here to help you understand your rights.\n\nI can assist you with questions about:\n• Workplace rights (Labour Relations Act, BCEA)\n• Consumer protection (Consumer Protection Act)\n• Housing and tenant rights (Rental Housing Act, PIE Act)\n• Constitutional rights (Bill of Rights)\n• Criminal law and your rights when dealing with police\n• Family law (Maintenance Act, Domestic Violence Act)\n• Privacy and data protection (POPIA)\n\nCould you provide more details about your specific situation? The more context you give, the more accurate and helpful my response will be.",
                    isUser = false,
                    citations = listOf(
                        LegalCitation(
                            actName = "Constitution of the Republic of South Africa",
                            actNumber = "Act 108 of 1996",
                            section = "Chapter 2",
                            subsection = null,
                            fullReference = "Constitution Ch2",
                            shortReference = "Bill of Rights",
                            fullText = "The Bill of Rights is a cornerstone of democracy in South Africa. It enshrines the rights of all people in our country and affirms the democratic values of human dignity, equality and freedom."
                        )
                    ),
                    confidence = Confidence.MEDIUM,
                    suggestedFollowUps = listOf(
                        "What are my rights if I'm arrested?",
                        "Can my employer fire me without a hearing?",
                        "What does POPIA mean for my personal data?"
                    )
                )
            }
        }
    }
}
