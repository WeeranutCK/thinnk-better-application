package cs211.project.services;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class AdjustImageView {
    public static void radiusImageView(ImageView imageView, double radius) {
        Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
        clip.setArcWidth(radius);
        clip.setArcHeight(radius);
        imageView.setClip(clip);
    }

    public static void setViewPortImageView(ImageView imageView, Image image) {
        double viewportHeight;
        double viewportMinX;
        double viewportMinY;
        double viewportWidth;

        double imageHeight = image.getHeight();
        double imageWidth = image.getWidth();
        double imageViewHeight = imageView.getFitHeight();
        double imageViewWidth = imageView.getFitWidth();

        if (imageWidth / imageHeight < imageViewWidth / imageViewHeight) {
            viewportMinX = 0;
            viewportWidth = imageWidth;
            viewportHeight = imageWidth / (imageViewWidth / imageViewHeight);
            viewportMinY = (imageHeight - viewportHeight) / 2;
        } else {
            viewportMinY = 0;
            viewportHeight = imageHeight;
            viewportWidth = imageHeight / (imageViewHeight / imageViewWidth);
            viewportMinX = (imageWidth - viewportWidth) / 2;
        }

        Rectangle2D crop = new Rectangle2D(viewportMinX, viewportMinY, viewportWidth, viewportHeight);
        imageView.setViewport(crop);
    }
}
