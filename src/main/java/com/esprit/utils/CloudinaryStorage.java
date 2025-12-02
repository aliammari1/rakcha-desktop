package com.esprit.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A service class for handling image uploads to Cloudinary.
 * This class encapsulates all Cloudinary operations and provides a simplified
 * API
 * for the rest of the application.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class CloudinaryStorage {

    private static final Logger LOGGER = Logger.getLogger(CloudinaryStorage.class.getName());
    private static CloudinaryStorage instance;
    private Cloudinary cloudinary;

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the Cloudinary object with configuration from environment
     * variables.
     */
    private CloudinaryStorage() {
        try {
            Dotenv dotenv = Dotenv.load();
            cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize Cloudinary client", e);
            throw new RuntimeException("Failed to initialize Cloudinary storage service", e);
        }

    }


    /**
     * Gets the singleton instance of CloudinaryStorage.
     *
     * @return the CloudinaryStorage instance
     */
    public static synchronized CloudinaryStorage getInstance() {
        if (instance == null) {
            instance = new CloudinaryStorage();
        }

        return instance;
    }


    /**
     * Uploads an image file to Cloudinary.
     *
     * @param imageFile the file to upload
     * @return the secure URL of the uploaded image
     * @throws IOException if the upload fails
     */
    public String uploadImage(File imageFile) throws IOException {
        if (imageFile == null) {
            throw new IllegalArgumentException("Image file cannot be null");
        }


        try {
            // Upload the file to Cloudinary
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(imageFile,
                ObjectUtils.emptyMap());

            // Get the secure URL of the uploaded image
            String imageUrl = (String) uploadResult.get("secure_url");

            LOGGER.info("Image uploaded to Cloudinary: " + imageUrl);
            return imageUrl;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error uploading image to Cloudinary", e);
            throw e;
        }

    }


    /**
     * Uploads an image file to Cloudinary with custom options.
     *
     * @param imageFile the file to upload
     * @param options   custom upload options
     * @return the secure URL of the uploaded image
     * @throws IOException if the upload fails
     */
    public String uploadImage(File imageFile, Map<String, Object> options) throws IOException {
        if (imageFile == null) {
            throw new IllegalArgumentException("Image file cannot be null");
        }


        try {
            // Upload the file to Cloudinary with the provided options
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(imageFile, options);

            // Get the secure URL of the uploaded image
            String imageUrl = (String) uploadResult.get("secure_url");

            LOGGER.info("Image uploaded to Cloudinary with custom options: " + imageUrl);
            return imageUrl;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error uploading image to Cloudinary with custom options", e);
            throw e;
        }

    }


    /**
     * Deletes an image from Cloudinary by its public ID.
     *
     * @param publicId the public ID of the image to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteImage(String publicId) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) cloudinary.uploader().destroy(publicId,
                ObjectUtils.emptyMap());
            String status = (String) result.get("result");
            return "ok".equals(status);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error deleting image from Cloudinary", e);
            return false;
        }

    }


    /**
     * Extracts the public ID from a Cloudinary URL.
     *
     * @param cloudinaryUrl the full Cloudinary URL
     * @return the public ID of the image
     */
    public String getPublicIdFromUrl(String cloudinaryUrl) {
        if (cloudinaryUrl == null || cloudinaryUrl.isEmpty()) {
            return null;
        }


        // Extract the filename without extension
        String filename = cloudinaryUrl.substring(cloudinaryUrl.lastIndexOf("/") + 1);
        if (filename.contains(".")) {
            filename = filename.substring(0, filename.lastIndexOf("."));
        }


        return filename;
    }

}

