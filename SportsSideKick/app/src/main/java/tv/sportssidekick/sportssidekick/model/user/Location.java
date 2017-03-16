package tv.sportssidekick.sportssidekick.model.user;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonPropertyOrder({
//        "country",
//        "city",
//        "latitide",
//        "longditute"
//})
public class Location {

    @JsonProperty("country")
    private String country;
    @JsonProperty("city")
    private String city;
    @JsonProperty("latitide")
    private Float latitide;
    @JsonProperty("longditute")
    private Float longditute;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
    }

    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    @JsonProperty("city")
    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("latitide")
    public Float getLatitide() {
        return latitide;
    }

    @JsonProperty("latitide")
    public void setLatitide(Float latitide) {
        this.latitide = latitide;
    }

    @JsonProperty("longditute")
    public Float getLongditute() {
        return longditute;
    }

    @JsonProperty("longditute")
    public void setLongditute(Float longditute) {
        this.longditute = longditute;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}