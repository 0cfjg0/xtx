package com.itheima.xiaotuxian.formart;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author lbc
 * @date 2023年5月8日11:42:49
 */
public class PriceFormart extends JsonSerializer<BigDecimal> {
    private DecimalFormat df = new DecimalFormat("#0.00");

    public PriceFormart() {
    }

    public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeString(this.df.format(value));
    }

}
