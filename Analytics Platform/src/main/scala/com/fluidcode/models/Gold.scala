package com.fluidcode.models

import java.sql.Date

case class BiggestSpenders(
                      customerId: String,
                      totalPrice: Double,
                      processingDate: java.sql.Timestamp
                    )


case class MostSoldProduct(
                                productId: String,
                                totalQuantity: Int,
                                processingDate: java.sql.Timestamp
                              )

