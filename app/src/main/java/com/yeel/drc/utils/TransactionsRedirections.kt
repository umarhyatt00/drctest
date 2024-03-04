package com.yeel.drc.utils

import android.content.Context
import android.content.Intent
import com.yeel.drc.R
import com.yeel.drc.activity.main.billpayments.internal.EducationInternalBillPaymentsActivity
import com.yeel.drc.activity.main.cashout.CashOutAmountEnteringActivity
import com.yeel.drc.activity.main.cashpickup.CashPickupDetailsActivity
import com.yeel.drc.activity.main.exchange.ExchangeCreditReceiptActivity
import com.yeel.drc.activity.main.exchange.ExchangeDebitReceiptActivity
import com.yeel.drc.activity.main.homepayandsend.AmountEnteringActivity
import com.yeel.drc.activity.main.mobilepay.MobilePayEstimateActivity
import com.yeel.drc.activity.main.receipt.credit.AgentSendViaYeelCreditReceipt
import com.yeel.drc.activity.main.receipt.credit.EducationCreditDetailsActivity
import com.yeel.drc.activity.main.receipt.credit.MobilePayCreditReceipt
import com.yeel.drc.activity.main.receipt.credit.MoneyRecivedReceiptActivity
import com.yeel.drc.activity.main.receipt.debit.*
import com.yeel.drc.api.recenttransactions.TransactionsData
import com.yeel.drc.api.userdetails.UserDetailsData
import com.yeel.drc.dialogboxes.ErrorDialog
import com.yeel.drc.model.BeneficiaryCommonData
import com.yeel.drc.model.billpayments.BillPaymentsData
import com.yeel.drc.model.cashpickup.CashPickupData
import com.yeel.drc.model.mpesa.MobilePayData

class TransactionsRedirections {

    companion object {
        @JvmStatic
        fun openTransactionDetailsScreen(
            context: Context, commonFunctions: CommonFunctions, data: TransactionsData
        ) {
            when (data.transaction_type) {
                SthiramValues.TRANSACTION_TYPE_USER_CASH_PICKUP -> {
                    val intent = Intent(context, CashPickupUserReciptActivity::class.java)
                    intent.putExtra("ydb_ref_id", data.ydb_ref_id)
                    context.startActivity(intent)
                }
                SthiramValues.TRANSACTION_TYPE_CASH_OUT -> {
                    val intent = Intent(context, CashoutReciptActivity::class.java)
                    intent.putExtra("ydb_ref_id", data.ydb_ref_id)
                    context.startActivity(intent)
                }
                SthiramValues.TRANSACTION_TYPE_YEEL_ACCOUNT, SthiramValues.TRANSACTION_TYPE_YEEL_ACCOUNT_MERCHANT -> {
                    val intent: Intent = if (commonFunctions.userId.equals(data.sender_id)) {
                        //debit
                        Intent(context, MoneySentReceiptActivity::class.java)
                    } else {
                        //credit
                        Intent(context, MoneyRecivedReceiptActivity::class.java)
                    }
                    intent.putExtra("ydb_ref_id", data.ydb_ref_id)
                    context.startActivity(intent)
                }
                SthiramValues.TRANSACTION_TYPE_AGENT_SEND_VIA_YEEL -> {
                    val intent = Intent(context, AgentSendViaYeelCreditReceipt::class.java)
                    intent.putExtra("ydb_ref_id", data.ydb_ref_id)
                    context.startActivity(intent)
                }
                SthiramValues.TRANSACTION_TYPE_UTILITIES_PAYMENTS -> {
                    val isExternal: String = data.providerData.is_external
                    val serviceType: String = data.providerData.service_type
                    val inOrOut: String = if (commonFunctions.userId.equals(data.sender_id)) {
                        "out"
                    } else {
                        "in"
                    }
                    if (isExternal != "1") {
                        if (inOrOut == "out") {
                            if (serviceType == SthiramValues.BILL_PAYMENT_TYPE_EDUCATION) {
                                val intent =
                                    Intent(context, EducationDebitDetailsActivity::class.java)
                                intent.putExtra("ydb_ref_id", data.ydb_ref_id)
                                context.startActivity(intent)
                            }
                        } else {
                            if (serviceType == SthiramValues.BILL_PAYMENT_TYPE_EDUCATION) {
                                val intent =
                                    Intent(context, EducationCreditDetailsActivity::class.java)
                                intent.putExtra("ydb_ref_id", data.ydb_ref_id)
                                context.startActivity(intent)
                            }
                        }
                    }

                }
                SthiramValues.TRANSACTION_TYPE_MPESA, SthiramValues.TRANSACTION_TYPE_AIRTEL_UGANDA -> {
                    val intent: Intent = if (commonFunctions.userId.equals(data.sender_id)) {
                        //debit
                        Intent(context, MobilePaySendReceiptActivity::class.java)
                    } else {
                        //credit
                        Intent(context, MobilePayCreditReceipt::class.java)
                    }
                    intent.putExtra("ydb_ref_id", data.ydb_ref_id)
                    context.startActivity(intent)
                }
                SthiramValues.TRANSACTION_TYPE_EXCHANGE -> {
                    val intent: Intent = if (SthiramValues.SELECTED_CURRENCY_SYMBOL.equals("SSP")) {
                        Intent(context, ExchangeCreditReceiptActivity::class.java)
                    } else {
                        Intent(context, ExchangeDebitReceiptActivity::class.java)
                    }
                    intent.putExtra("ydb_ref_id", data.ydb_ref_id)
                    context.startActivity(intent)
                }
            }
        }


        @JvmStatic
        fun sendAgainFromTransactionList(
            context: Context,
            commonFunctions: CommonFunctions,
            errorDialog: ErrorDialog,
            data: TransactionsData,
        ) {
            when (data.transaction_type) {
                SthiramValues.TRANSACTION_TYPE_USER_CASH_PICKUP -> {
                    val cashPickupData = CashPickupData()
                    val cashPickupReceiverData = BeneficiaryCommonData()
                    cashPickupReceiverData.firstName = data.beneficiaryDetails.firstname
                    cashPickupReceiverData.middleName = data.beneficiaryDetails.middleName
                    cashPickupReceiverData.lastName = data.beneficiaryDetails.lastname
                    cashPickupReceiverData.mobileNumber = data.beneficiaryDetails.mobile
                    cashPickupReceiverData.mobileCountryCode =
                        context.getString(R.string.country_code)
                    cashPickupReceiverData.emailAddress = data.beneficiaryDetails.email
                    cashPickupReceiverData.countryName = data.beneficiaryDetails.country
                    cashPickupReceiverData.beneficiaryId = data.beneficiaryDetails.id
                    cashPickupData.cashPickupReceiverData = cashPickupReceiverData
                    cashPickupData.cashPickupType = SthiramValues.TRANSACTION_TYPE_USER_CASH_PICKUP
                    val intent = Intent(context, CashPickupDetailsActivity::class.java)
                    intent.putExtra("data", cashPickupData)
                    context.startActivity(intent)
                }
                SthiramValues.TRANSACTION_TYPE_CASH_OUT -> {
                    val currencyList =
                        data.currencyList.filter { it.currency_id.equals(commonFunctions.currencyID) }
                    if (currencyList.size == 1) {
                        data.agentDetails.account_no = currencyList[0].account_number
                        val intent = Intent(context, CashOutAmountEnteringActivity::class.java)
                        intent.putExtra("data", data.agentDetails)
                        context.startActivity(intent)
                    } else {
                        val message: String =
                            context.getString(R.string.receiver_not_matching_message) + SthiramValues.SELECTED_CURRENCY_SYMBOL + context.getString(
                                R.string.receiver_not_matching_message_2
                            )
                        errorDialog.show(message)
                    }
                }
                SthiramValues.TRANSACTION_TYPE_YEEL_ACCOUNT, SthiramValues.TRANSACTION_TYPE_YEEL_ACCOUNT_MERCHANT -> {
                    val type: String =
                        if (data.transaction_type.equals(SthiramValues.TRANSACTION_TYPE_YEEL_ACCOUNT_MERCHANT)) {
                            SthiramValues.ACCOUNT_TYPE_BUSINESS
                        } else {
                            SthiramValues.ACCOUNT_TYPE_INDIVIDUAL
                        }
                    val userDetails = UserDetailsData(
                        data.receiver_name,
                        data.profile_image,
                        data.receiver_account_no,
                        data.receiver_id,
                        type
                    )
                    val intent = Intent(context, AmountEnteringActivity::class.java)
                    intent.putExtra("from", "SendAgain")
                    intent.putExtra("data", userDetails)
                    context.startActivity(intent)
                }
                SthiramValues.TRANSACTION_TYPE_UTILITIES_PAYMENTS -> {
                    val isExternal: String = data.providerData.is_external
                    val serviceType: String = data.providerData.service_type
                    val transactionsData: TransactionsData = data
                    val billPaymentsData = BillPaymentsData()
                    val receiverAccountNumber =
                        commonFunctions.getReceiverAccountNumberFormCurrencyList(transactionsData.providerCurrencyList)
                    if (receiverAccountNumber != null && !receiverAccountNumber.equals("")) {
                        transactionsData.providerData.accountNumber = receiverAccountNumber
                        billPaymentsData.providersListData = transactionsData.providerData
                        billPaymentsData.studentName = transactionsData.billPaymentDetails.name
                        billPaymentsData.studentId = transactionsData.billPaymentDetails.id
                        billPaymentsData.studentMobileNumber =
                            transactionsData.billPaymentDetails.mobile
                        billPaymentsData.studentMobileCountyCode =
                            transactionsData.billPaymentDetails.countyCode
                        if (isExternal != "1") {
                            if (serviceType == SthiramValues.BILL_PAYMENT_TYPE_EDUCATION) {
                                val intent = Intent(
                                    context,
                                    EducationInternalBillPaymentsActivity::class.java
                                )
                                intent.putExtra("data", billPaymentsData)
                                intent.putExtra("from", "normal")
                                context.startActivity(intent)
                            }
                        }
                    } else {
                        val message: String =
                            context.getString(R.string.receiver_not_matching_message) + SthiramValues.SELECTED_CURRENCY_SYMBOL + context.getString(
                                R.string.receiver_not_matching_message_2
                            )
                        errorDialog.show(message)
                    }
                }
                SthiramValues.TRANSACTION_TYPE_MPESA, SthiramValues.TRANSACTION_TYPE_AIRTEL_UGANDA -> {
                    var buttonStatus = false
                    val mobilePayData = MobilePayData()
                    if (data.transaction_type.equals(SthiramValues.TRANSACTION_TYPE_MPESA)) {
                        mobilePayData.mobilePayName = "M-Pesa Kenya"
                        mobilePayData.mobilePayCode = "MP"
                        if (commonFunctions.mpesaKenyaStatus.equals("1")) {
                            buttonStatus = true
                        }
                    } else if (data.transaction_type.equals(SthiramValues.TRANSACTION_TYPE_AIRTEL_UGANDA)) {
                        mobilePayData.mobilePayName = "Airtel Uganda"
                        mobilePayData.mobilePayCode = "AU"
                        if (commonFunctions.airtelUgandaStatus.equals("1")) {
                            buttonStatus = true
                        }
                    }
                    if (buttonStatus) {
                        val intent = Intent(
                            context,
                            MobilePayEstimateActivity::class.java
                        )
                        intent.putExtra("data", mobilePayData)
                        context.startActivity(intent)
                    } else {
                        errorDialog.show(context.getString(R.string.send_again_disabled_message))
                    }
                }
                SthiramValues.TRANSACTION_TYPE_EXCHANGE -> {
                     if(commonFunctions.exchangeStatus.equals("1")){
                         val intent = Intent(
                             context,
                             MobilePayEstimateActivity::class.java
                         )
                         context.startActivity(intent)
                     }else{
                         errorDialog.show(context.getString(R.string.send_again_disabled_message))
                     }
                }
            }
        }
    }


}