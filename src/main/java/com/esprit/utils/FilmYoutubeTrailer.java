package com.esprit.utils;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
public class FilmYoutubeTrailer {
    private final String API_KEY = "AIzaSyABEi2834N8l6Cty8yFCEiGRisZjyXonEM";
    /** 
     * @param filmNom
     * @return String
     */
    public String watchTrailer(String filmNom) {
        System.out.println("watch the trailer");
        try {
            YouTube youtube = new YouTube.Builder(
                    new NetHttpTransport(),
                    new JacksonFactory(),
                    request -> {
                    })
                    .setApplicationName("Rakcha")
                    .build();
            System.out.println("the trailer is not watched");
            YouTube.Search.List search = youtube.search().list("id,snippet");
            search.setKey(API_KEY);
            search.setQ(filmNom);
            search.setType("video");
            search.setFields("items(id/videoId)");
            search.setMaxResults(1L);
            SearchResult searchResult = search.execute().getItems().get(0);
            String videoId = searchResult.getId().getVideoId();
            return "https://www.youtube.com/embed/" + videoId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "https://www.youtube.com";
    }
}
