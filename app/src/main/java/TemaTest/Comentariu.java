package TemaTest;

import java.io.*;
import java.util.ArrayList;

public class Comentariu {
    String text;
    private int id = 0;
    private int idPostare;
    private static int idCount = 1;
    private String user, password;
    private int nrLikes;


    public Comentariu(String text, int idPostare, String user, String password) {
        this.text = text;
        this.id = idCount++;
        this.idPostare = idPostare;
        this.user = user;
        this.password = password;
        this.nrLikes = 0;
    }

    public static void clean() {
        try (PrintWriter postareWriter = new PrintWriter(new FileWriter("comentariu.csv"))) {
            postareWriter.print("");
            idCount = 1;
            System.out.println("All post data cleaned up!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean textLength() {
        if (text.length() <= 300)
            return true;
        else
            return false;
    }

    public void writeToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("comentariu.csv", true))) {
            writer.write(idPostare + "," + user + "," + password + "," + id + "," + text + "," + nrLikes + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteCommByIdFromFile(int postId, String username, String password) {
        ArrayList<String> tempPosts = new ArrayList<>();
        boolean postFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader("comentariu.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int currentPostId = Integer.parseInt(parts[3]);
                if (!username.equals(parts[1]) || !password.equals(parts[2])) {
                    System.out.println("{'status':'error','message':'The identifier was not valid'}");
                    return;
                }
                if (currentPostId != postId) {
                    tempPosts.add(line);
                } else {
                    postFound = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!postFound) {
            System.out.println("{'status':'error','message':'The identifier was not valid'}");
            // tempPosts.clear();
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter("comentariu.csv"))) {
            for (String post : tempPosts) {
                writer.println(post);
            }
            System.out.println("{'status':'ok','message':'Operation executed successfully'}");
        } catch (IOException e) {
            e.printStackTrace();
        }
        tempPosts.clear();
    }

    public void like(int commId, String username) {
        ArrayList<String> tempComm = new ArrayList<>();
        boolean commFound = false;

        if (userAlreadyLike(username, commId)) {
            System.out.println("{'status':'error','message':'The comment identifier to like was not valid'}");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("comentariu.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int currentCommId = Integer.parseInt(parts[0]);

                if (currentCommId == commId) {
                    // Gasit postul cu ID-ul specificat in fisier
                    int currentNrLikes = Integer.parseInt(parts[5]);
                    currentNrLikes++; // crestere nrLikes
                    this.nrLikes = currentNrLikes;
                    line = currentCommId + "," + parts[1] + "," + parts[2] + "," + parts[3] + "," + parts[4] + "," + currentNrLikes; // actualizare linie
                    commFound = true;
                }

                tempComm.add(line); // adaug linia intr-o linie temporara
            }

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }


        if (commFound) {
            System.out.println("{'status':'ok','message':'Operation executed successfully'}");
        } else {
            System.out.println("{'status':'error','message':'The comment identifier to like was not valid'}");
            return;
        }

        try (PrintWriter likeWriter = new PrintWriter(new FileWriter("likeComentariu.csv", true))) {
            likeWriter.println(commId + "," + username + "," + nrLikes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter("comentariu.csv"))) {
            for (String post : tempComm) {
                writer.println(post);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        tempComm.clear();
    }

    private boolean userAlreadyLike(String username, int id) {
        try (BufferedReader reader = new BufferedReader(new FileReader("likeComentariu.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int currentId = Integer.parseInt(parts[0]);
                if (parts[1].equals(username) && currentId == id) {
                    return true; // user-ul exista
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void unlike(int postId, String username) {
        ArrayList<String> tempPosts = new ArrayList<>();
        ArrayList<String> tempLikes = new ArrayList<>();
        boolean postFound = false;

        try (BufferedReader readerLikes = new BufferedReader(new FileReader("likeComentariu.csv"))) {
            String lineLike;
            while ((lineLike = readerLikes.readLine()) != null) {
                String[] parts = lineLike.split(",");
                int currentPostId = Integer.parseInt(parts[0]);
                if (currentPostId == postId && username.equals(parts[1])) {
                    // Gasit postul cu ID-ul specificat in fisierul like.csv
                    int currentNrLikes = Integer.parseInt(parts[2]);
                    currentNrLikes--; // scadere nrLikes
                    this.nrLikes = currentNrLikes;
                    lineLike = currentPostId + "," + parts[1] + "," + currentNrLikes;
                    postFound = true;
                } else {
                    tempLikes.add(lineLike);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader readerPosts = new BufferedReader(new FileReader("comentariu.csv"))) {
            String linePosts;
            while ((linePosts = readerPosts.readLine()) != null) {
                String[] partsPosts = linePosts.split(",");
                int currentPostIdPosts = Integer.parseInt(partsPosts[0]);
                if (currentPostIdPosts == postId) {
                    int currentNrLikes = Integer.parseInt(partsPosts[5]);
                    currentNrLikes--; // scadere nrLikes
                    linePosts = currentPostIdPosts + "," + partsPosts[1] + "," + partsPosts[2] + "," + partsPosts[3] + "," + partsPosts[4] + "," + currentNrLikes; // actualizare linie
                }
                tempPosts.add(linePosts);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!postFound) {
            System.out.println("{'status':'error','message':'The comment identifier to unlike was not valid'}");
            tempPosts.clear();
            tempLikes.clear();
            return;
        } else {
            System.out.println("{'status':'ok','message':'Operation executed successfully'}");
        }

        try (PrintWriter writerPosts = new PrintWriter(new FileWriter("comentariu.csv"))) {
            for (String post : tempPosts) {
                writerPosts.println(post);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        tempPosts.clear();

        try (PrintWriter writerLikes = new PrintWriter(new FileWriter("likeComentariu.csv"))) {
            for (String like : tempLikes) {
                writerLikes.println(like);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        tempLikes.clear();
    }

}
