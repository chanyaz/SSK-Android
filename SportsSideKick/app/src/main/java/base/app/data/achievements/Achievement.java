package base.app.data.achievements;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Filip on 3/26/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

/*
    This is a temporary class that I've loosely based off GameSparks acheivements setup. It may be that GameSparks have an SDK with this class already in
    or this class might just need to be hooked into GameSparks for the back-end.
*/


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Achievement {

//      original from iOS:
//        var shortName : String
//        var localizedName : String
//        var localizedType : String
//        var localizedDescription : String
//        var earnedDate : Date
//        var iconURL : URL


    @JsonProperty("description")
    private String description;
    @JsonProperty("earned")
    private Boolean earned;
    @JsonProperty("name")
    private String name;
    @JsonProperty("propertySet")
    private Object propertySet;
    @JsonProperty("shortCode")
    private String shortCode;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("earned")
    public Boolean getEarned() {
        return earned;
    }

    @JsonProperty("earned")
    public void setEarned(Boolean earned) {
        this.earned = earned;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("propertySet")
    public Object getPropertySet() {
        return propertySet;
    }

    @JsonProperty("propertySet")
    public void setPropertySet(Object propertySet) {
        this.propertySet = propertySet;
    }

    @JsonProperty("shortCode")
    public String getShortCode() {
        return shortCode;
    }

    @JsonProperty("shortCode")
    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
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


