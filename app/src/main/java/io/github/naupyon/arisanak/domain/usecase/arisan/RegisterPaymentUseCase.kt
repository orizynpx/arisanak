package io.github.naupyon.arisanak.domain.usecase.arisan

import io.github.naupyon.arisanak.domain.model.PaymentLog
import io.github.naupyon.arisanak.domain.repository.ArisanRepository
import javax.inject.Inject

class RegisterPaymentUseCase @Inject constructor(
    private val repository: ArisanRepository
) {
    suspend operator fun invoke(memberId: Long, intervalId: Long, amount: Double, isDitalangi: Boolean, imagePath: String? = null) {
        val log = PaymentLog(
            memberId = memberId,
            intervalId = intervalId,
            amountPaid = amount,
            isDitalangi = isDitalangi,
            receiptImagePath = imagePath
        )
        repository.insertPaymentLog(log)
    }
}
