package gbabazaar.gbabazaar;

/**
 * Created by harsh on 07-Jul-16.
 */
public class Item {

    private String title;
    private String category;
    private String objectId;
    private byte[] imageBitmap;

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

    public byte[] gettImageBitmap() {
        return imageBitmap;
    }

    public void settImageBitmap(byte[] imageBitmap) {
        this.imageBitmap = imageBitmap;
    }
}
