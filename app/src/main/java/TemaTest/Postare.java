package TemaTest;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Postare implements Likeable {
    private String text;
    private int id = 0;
    private static int idCount = 1;
    private int nrLikes;
    private String user;
    public Postare(String text, String user) {
        this.text = text;
        this.id = idCount++;
        this.user = user;
        this.nrLikes = 0;
    }

    public static void clean() {
        try (PrintWriter postareWriter = new PrintWriter(new FileWriter("postare.csv"))) {
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


    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }


    public int getNumarLikeuri() {
        return nrLikes;
    }

    public void writeToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("postare.csv", true))) {
            writer.write(id + "," + user + "," + text + "," + nrLikes + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Metoda pentru a sterge postarea din fisier in functie de ID i -- merge in consola
    public static void deletePostByIdFromFile(int postId) {
        ArrayList<String> tempPosts = new ArrayList<>();
        boolean postFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader("postare.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int currentPostId = Integer.parseInt(parts[0]);
                if (currentPostId != postId) {
                    // Adauga celelalte postari in lista (cu exceptia celei cu ID-ul specificat)
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
            tempPosts.clear();
            return;
        } else {
            System.out.println("{'status':'ok','message':'Post deleted successfully'}");
        }
        // Suprascrie fisierul postare.csv cu lista actualizata
        try (PrintWriter writer = new PrintWriter(new FileWriter("postare.csv"))) {
            for (String post : tempPosts) {
                writer.println(post);
            }
            //   System.out.println("{'status':'ok','message':'Post deleted successfully'}");
        } catch (IOException e) {
            e.printStackTrace();
        }
        tempPosts.clear();
    }

    // Metoda pentru a obtine o postare in functie de ID
//    public static Postare getPostById(int postId) {
//        for (Postare post : postList) {
//            if (post.getId() == postId) {
//                return post;
//            }
//        }
//        return null;
//    }

    public void like(int postId, String username) {
        ArrayList<String> tempPosts = new ArrayList<>();
        boolean postFound = false;

        if(userAlreadyLike(username, postId)) {
            System.out.println("{'status':'error','message':'The post identifier to like was not valid'}");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("postare.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int currentPostId = Integer.parseInt(parts[0]);

                if (currentPostId == postId) {
                    // Gasit postul cu ID-ul specificat in fisier
                    int currentNrLikes = Integer.parseInt(parts[3]); // presupunand ca nrLikes este pe pozitia 3
                    currentNrLikes++; // crestere nrLikes
                    this.nrLikes = currentNrLikes;
                    line = currentPostId + "," + parts[1] + "," +  parts[2]+ "," + currentNrLikes; // actualizare linie
                    postFound = true;
                }

                tempPosts.add(line); // adaugare linie in lista temporara
            }

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }


            if (postFound) {
                System.out.println("{'status':'ok','message':'Operation executed successfully'}");
            } else {
                System.out.println("{'status':'error','message':'The post identifier to like was not valid'}");
                return;
            }

        try (PrintWriter likeWriter = new PrintWriter(new FileWriter("likePostare.csv", true))) {
            likeWriter.println(postId + "," + username + "," + nrLikes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter("postare.csv"))) {
            for (String post : tempPosts) {
                writer.println(post);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        tempPosts.clear();
    }
    private boolean userAlreadyLike(String username, int id) {
        try (BufferedReader reader = new BufferedReader(new FileReader("likePostare.csv"))) {
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

        try (BufferedReader readerLikes = new BufferedReader(new FileReader("likePostare.csv"))) {
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
                    tempLikes.add(lineLike); // adaugare linie in lista temporara
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader readerPosts = new BufferedReader(new FileReader("postare.csv"))) {
            String linePosts;
            while ((linePosts = readerPosts.readLine()) != null) {
                String[] partsPosts = linePosts.split(",");
                int currentPostIdPosts = Integer.parseInt(partsPosts[0]);
                if (currentPostIdPosts == postId ) {
                    // Gasit postul cu ID-ul specificat in fisierul postare.csv
                    int currentNrLikes = Integer.parseInt(partsPosts[3]);
                    currentNrLikes--; // scadere nrLikes
                    linePosts = currentPostIdPosts + "," + partsPosts[1] + "," + partsPosts[2] + "," + currentNrLikes; // actualizare linie
                }
                tempPosts.add(linePosts); // adaugare linie in lista temporara
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!postFound) {
            System.out.println("{'status':'error','message':'The post identifier to unlike was not valid'}");
            tempPosts.clear();
            tempLikes.clear();
            return;
        } else {
            System.out.println("{'status':'ok','message':'Operation executed successfully'}");
        }

        // Suprascrie fisierul postare.csv cu lista actualizata
        try (PrintWriter writerPosts = new PrintWriter(new FileWriter("postare.csv"))) {
            for (String post : tempPosts) {
                writerPosts.println(post);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        tempPosts.clear();

        // Suprascrie fisierul like.csv cu lista actualizata
        try (PrintWriter writerLikes = new PrintWriter(new FileWriter("likePostare.csv"))) {
            for (String like : tempLikes) {
                writerLikes.println(like);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        tempLikes.clear();
    }

    /* lista postari peroane urmarite */


    public static void followedListPostDate(String username) {
        boolean aux = true;
        try (BufferedReader readerLikes = new BufferedReader(new FileReader("follows.csv"))) {
            String line;
            while ((line = readerLikes.readLine()) != null) {
                String[] parts = line.split(",");
                if (username.equals(parts[0])) {
                    String userFollowed = parts[1];

                    try (BufferedReader postReader = new BufferedReader(new FileReader("postare.csv"))) {
                        String postLine;
                        while ((postLine = postReader.readLine()) != null) {
                            String[] postParts = postLine.split(",");
                            if (userFollowed.equals(postParts[1])) {
                                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                Date date = new Date();
                                String currentDateAsString = dateFormat.format(date);

                                if (aux) {
                                    System.out.print("{ 'status' : 'ok', 'message' : [");
                                    aux = false;
                                } else {
                                    System.out.print(",");
                                }

                                System.out.print("{''" + postParts[0] + "', 'post_text' : '" + postParts[2]
                                        + "', 'post_date' : '" + currentDateAsString + "', 'username' : '" + postParts[1] + "'}");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!aux) {
            System.out.print("]}");
        }
    }


}


