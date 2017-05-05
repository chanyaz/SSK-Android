package tv.sportssidekick.sportssidekick.model.wall;

import com.facebook.ads.AdNetwork;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdViewAttributes;

import tv.sportssidekick.sportssidekick.model.sharing.SharingManager;

/**
 * Created by Nemanja Jovanovic on 04/05/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WallAdvert extends WallBase {
    private String id;
    private String title;
    private String subtitle;
    private String body;
    private String callToAction;
    private String choicesLinkUrl;
    private String socialContext;
    private String placementId;

    private NativeAd.Image choicesIcon;
    private NativeAd.Image coverImage;
    private NativeAd.Image icon;

    private AdNetwork network;

    private NativeAdViewAttributes viewAttributes;

    private NativeAd nativeAd;

    public WallAdvert() {
    }

    public NativeAd getNativeAd() {
        return nativeAd;
    }

    public void setNativeAd(NativeAd nativeAd) {
        this.nativeAd = nativeAd;
        this.setId(nativeAd.getId());
        this.setTitle(nativeAd.getAdTitle());
        this.setSubtitle(nativeAd.getAdSubtitle());
        this.setBody(nativeAd.getAdBody());
        this.setCallToAction(nativeAd.getAdCallToAction());
        this.setChoicesLinkUrl(nativeAd.getAdChoicesLinkUrl());
        this.setSocialContext(nativeAd.getAdSocialContext());
        this.setPlacementId(nativeAd.getPlacementId());
        this.setChoicesIcon(nativeAd.getAdChoicesIcon());
        this.setCoverImage(nativeAd.getAdCoverImage());
        this.setIcon(nativeAd.getAdIcon());
        this.setNetwork(nativeAd.getAdNetwork());
        this.setViewAttributes(nativeAd.getAdViewAttributes());
        this.setType(WallBase.PostType.nativeAd);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCallToAction() {
        return callToAction;
    }

    public void setCallToAction(String callToAction) {
        this.callToAction = callToAction;
    }

    public String getChoicesLinkUrl() {
        return choicesLinkUrl;
    }

    public void setChoicesLinkUrl(String choicesLinkUrl) {
        this.choicesLinkUrl = choicesLinkUrl;
    }

    public String getSocialContext() {
        return socialContext;
    }

    public void setSocialContext(String socialContext) {
        this.socialContext = socialContext;
    }

    public String getPlacementId() {
        return placementId;
    }

    public void setPlacementId(String placementId) {
        this.placementId = placementId;
    }

    public NativeAd.Image getChoicesIcon() {
        return choicesIcon;
    }

    public void setChoicesIcon(NativeAd.Image choicesIcon) {
        this.choicesIcon = choicesIcon;
    }

    public NativeAd.Image getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(NativeAd.Image coverImage) {
        this.coverImage = coverImage;
    }

    public NativeAd.Image getIcon() {
        return icon;
    }

    public void setIcon(NativeAd.Image icon) {
        this.icon = icon;
    }

    public AdNetwork getNetwork() {
        return network;
    }

    public void setNetwork(AdNetwork network) {
        this.network = network;
    }

    public NativeAdViewAttributes getViewAttributes() {
        return viewAttributes;
    }

    public void setViewAttributes(NativeAdViewAttributes viewAttributes) {
        this.viewAttributes = viewAttributes;
    }

    @Override
    public SharingManager.ItemType getItemType() {
        return null;
    }
}
