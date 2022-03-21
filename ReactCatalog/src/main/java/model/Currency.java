package model;

public enum Currency {
    RUB(1.0), USD(107.50), EUR(119.01);

    private final double rate;

    Currency(double rate) {
        this.rate = rate;
    }

    public static Currency getCurrencyByCode(String currencyCode) {
        return Currency.valueOf(currencyCode);
    }

    public String getCurrencyCode(){
        return this.toString();
    }

    public static double convert(double value, Currency from, Currency to) {
        return value * from.rate / to.rate;
    }
}
