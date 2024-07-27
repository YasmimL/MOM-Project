package br.com.ifce.model;

public record Sensor(String topicName, Parameter parameter, Double minLimit, Double maxLimit, Double currentValue) {
    public Sensor withMinLimit(Double minLimit) {
        return new Sensor(
            this.topicName,
            this.parameter,
            minLimit,
            this.maxLimit,
            this.currentValue
        );
    }

    public Sensor withMaxLimit(Double maxLimit) {
        return new Sensor(
            this.topicName,
            this.parameter,
            this.minLimit,
            maxLimit,
            this.currentValue
        );
    }

    public Sensor withCurrentValue(Double currentValue) {
        return new Sensor(
            this.topicName,
            this.parameter,
            this.minLimit,
            this.maxLimit,
            currentValue
        );
    }

    public boolean someLimitReached() {
        if (this.currentValue < this.minLimit) return true;
        if (this.currentValue > this.maxLimit) return true;

        return false;
    }
}
