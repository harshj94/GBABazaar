package gbabazaar.gbabazaar;

/**
 * Created by harsh on 07-Jul-16.
 */
public class Item {

    private String title;
    private String category;
    private String objectId;
    private String url;

    public String gettTitle() {
        return title;
    }

    public void settTitle(String title) {
        this.title = title;
    }

    public String gettCategory() {
        return category;
    }

    public void settCategory(String category) {
        this.category = category;
    }

    public String gettObjectId() {
        return objectId;
    }

    public void settObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String gettURL() {
        return url;
    }

    public void settURL(String url) {
        this.url = url;
    }

}
