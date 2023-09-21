package com.fluidcode.models
import java.sql.Date

case class Orders(
                        orderId: String,
                        orderLine: Int,
                        orderDate: Date,  // date en format standard SQL Date
                        productId: String,
                        quantity: Int,
                        seasonality: String, // nouvel indicateur de saisonnalité
                        processingDate: java.sql.Timestamp
                      )



case class customers(
                                orderId: String,
                                customerId: String,
                                isWeekend: Boolean,  // est-ce que la commande a été passée pendant le week-end?
                                totalPrice: Double,  // prix total, si vous avez accès à des données de prix
                                processingDate: java.sql.Timestamp
                              )

