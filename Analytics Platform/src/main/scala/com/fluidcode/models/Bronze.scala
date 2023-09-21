package com.fluidcode.models

case class OrderBronze(
                        _c0: Int,
                        orderId: Int,
                        orderLine: Int,
                        orderDate: String, // date en format texte brut
                        customerId: String,
                        productId: String,
                        quantity: Int,
                        processingDate: java.sql.Timestamp
                      )
