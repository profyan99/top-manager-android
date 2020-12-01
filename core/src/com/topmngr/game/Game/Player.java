package com.topmngr.game.Game;

import com.topmngr.game.Network.Network;

/**
 * Created by PROFYAN on 25.01.2017.
 */
public class Player {
    public String
            name,
            color;
    public Network.PlayersState gState;
    public float
            gSell, // Продано
            gSellOld,
            gStorage, // Склад
            gBackLog, // Невыполненные заказы
            gRevenue, // Выручка
            gCostMakeProduct, //Стоимость производства еденицы товара
            gSPPT, // СППТ
            gCostMakeProductAll, // Стоимость производства
            gGrossIncome, // Валовый доход.
            gDepreciation, //  Амортизация
            gCostStorage, //Стоимость хранения
            gBankInterest, // Банковский процент
            gProfitTax, // Выручка до налога
            gTax, // налог
            gNetProfit, // Чистая прибыль
            gLoans, // займы
            gCash, // Наличные
            gAccumulatedProfit, // Накопленная прибыль
            gAccumulatedProfitZero,
            gFullPower, // Полная мощность
            gAdditionalValues, // Доп. вложения
            gFuturePower, // Мощность след.периода
            gUsingPower, // Используемая мощность
            gMachineTools, // Станки
            gActiveStorage, // Актив склада
            gKapInvests, // Капвложения
            gSumActive, // Суммарный актив

            gPlayerCost, // цена изм.
            gPlayerProduction, // Производство изм.
            gPlayerMarketing, // Маркетинг изм.
            gPlayerInvestments, // Инвестиции изм.
            gPlayerResAndDev, // НИОКР изм.

            gReceivedOrders,
            gReceivedOrdersOld,
            gRif,
            gAllResAndDev,
            gAllMarketing,
            gAllProduction,

            allReceivedOrders,
            allProd,
            allSell,
            allStorage,
            allRevenue,
            allAvCost,
            allAvCostMakeProd,
            allAvUsingPower,
            allKapInvests,
            allSumPower;

    public double
            gBuyersOld,
            gMarketShare;
    public int
            maxPeriods,
            currPeriod;
    public boolean
            isSend,
            isBankrupt,
            isBankruptSend;
}
