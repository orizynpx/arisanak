package io.github.naupyon.arisanak.domain.usecase.arisan

import io.github.naupyon.arisanak.domain.model.PaymentLog
import io.github.naupyon.arisanak.domain.model.PiutangLog
import io.github.naupyon.arisanak.domain.repository.ArisanRepository
import javax.inject.Inject

class ExecuteTalangiUseCase @Inject constructor(
    private val repository: ArisanRepository
) {
    suspend operator fun invoke(memberId: Long, intervalId: Long, requiredDue: Double) {
        val logs = repository.getPaymentLogsForIntervalOneShot(intervalId).filter { it.memberId == memberId }
        val totalPaid = logs.sumOf { it.amountPaid }
        val delta = requiredDue - totalPaid
        
        if (delta > 0) {
            val paymentLog = PaymentLog(
                memberId = memberId,
                intervalId = intervalId,
                amountPaid = delta,
                isDitalangi = true
            )
            repository.insertPaymentLog(paymentLog)

            val piutangLog = PiutangLog(
                memberId = memberId,
                amountDebt = delta,
                amountRepaid = 0.0,
                isSettled = false
            )
            repository.insertPiutangLog(piutangLog)
        }
    }
}
