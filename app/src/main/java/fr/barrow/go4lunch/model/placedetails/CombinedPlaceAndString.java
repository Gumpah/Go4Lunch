package fr.barrow.go4lunch.model.placedetails;

public class CombinedPlaceAndString {

    private PlaceDetailsResult placeDetailsResult;
    private String photoUrl;
    private String photo_reference;

    public CombinedPlaceAndString(PlaceDetailsResult placeDetailsResult, String photoUrl) {
        this.placeDetailsResult = placeDetailsResult;
        this.photoUrl = photoUrl;
    }

    public PlaceDetailsResult getPlaceDetailsResult() {
        return placeDetailsResult;
    }

    public void setPlaceDetailsResult(PlaceDetailsResult placeDetailsResult) {
        this.placeDetailsResult = placeDetailsResult;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoReference() {
        return photo_reference;
    }

    public void setPhotoReference(String photo_reference) {
        this.photo_reference = photo_reference;
    }
}
