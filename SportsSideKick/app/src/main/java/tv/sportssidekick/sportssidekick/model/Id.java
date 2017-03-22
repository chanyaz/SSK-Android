package tv.sportssidekick.sportssidekick.model;

/**
 * Created by Filip on 3/22/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Id {

    @JsonProperty("$oid")
    private String oid;

    @JsonProperty("$oid")
    public String getOid() {
        return oid;
    }

    @JsonProperty("$oid")
    public void setOid(String oid) {
        this.oid = oid;
    }
}

