package TemaTest;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Postare implements Likeable {
    private final String text;
    private final int id;
    private static int idCount = 1;
    private int nrLikes;
    private final String user;

    public Postare(String text, String user) {
        this.text = text;
        this.id = idCount++;
        this.user = user;
        this.nrLikes = 0;
    }

    public static void clean() {
        try (PrintWriter postareWriter = new PrintWriter(new FileWriter("postare.csv"))) {
            postareWriter.print("");
            idCount = 1; // resetare pt id
            System.out.println("Clean");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean textLength() {
        return text.length() <= 300;
    }

    public void writeToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("postare.csv", true))) {
            writer.write(id + "," + user + "," + text + "," + nrLikes + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // sterg postarea din fiser in functie de id -- merge in consola
    public static void deletePostByIdFromFile(int postId) {
        ArrayList<String> tempPosts = new ArrayList<>();
        boolean postFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader("postare.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int currentPostId = Integer.parseInt(parts[0]);
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
            tempPosts.clear();
            return;
        } else {
            System.out.println("{'status':'ok','message':'Post deleted successfully'}");
        }
        // suprascriu postare.csv cu lista actualizata stocata in arraylist
        try (PrintWriter writer = new PrintWriter(new FileWriter("postare.csv"))) {
            for (String post : tempPosts) {
                writer.println(post);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        tempPosts.clear();
    }

    public void like(int postId, String username) {
        ArrayList<String> tempPosts = new ArrayList<>();
        boolean postFound = false;

        if (userAlreadyLike(username, postId)) {
            System.out.println("{'status':'error','message':'The post identifier to like was not valid'}");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("postare.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int currentPostId = Integer.parseInt(parts[0]);

                if (currentPostId == postId) {
                    int currentNrLikes = Integer.parseInt(parts[3]);
                    currentNrLikes++; // crestere nrLikes
                    this.nrLikes = currentNrLikes;
                    line = currentPostId + "," + parts[1] + "," + parts[2] + "," + currentNrLikes; // actualizare linie
                    postFound = true;
                }

                tempPosts.add(line); // adaug linie in arraylist-ul temporar
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

        // scriu informatiile in alt fiser pentru ca am sa am nevoie la verificari ulterioare (pt unlike)
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
                    int currentNrLikes = Integer.parseInt(parts[2]);
                    currentNrLikes--; // scadere nrLikes
                    this.nrLikes = currentNrLikes;
                    lineLike = currentPostId + "," + parts[1] + "," + currentNrLikes;
                    postFound = true;
                }
                tempLikes.add(lineLike); // adaug linie in lista temporara
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader readerPosts = new BufferedReader(new FileReader("postare.csv"))) {
            String linePosts;
            while ((linePosts = readerPosts.readLine()) != null) {
                String[] partsPosts = linePosts.split(",");
                int currentPostIdPosts = Integer.parseInt(partsPosts[0]);
                if (currentPostIdPosts == postId) {
                    int currentNrLikes = Integer.parseInt(partsPosts[3]);
                    currentNrLikes--; // scadere nrLikes
                    linePosts = currentPostIdPosts + "," + partsPosts[1] + "," + partsPosts[2] + "," + currentNrLikes;
                }
                tempPosts.add(linePosts);
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


        try (PrintWriter writerPosts = new PrintWriter(new FileWriter("postare.csv"))) {
            for (String post : tempPosts) {
                writerPosts.println(post);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        tempPosts.clear();

        try (PrintWriter writerLikes = new PrintWriter(new FileWriter("likePostare.csv"))) {
            for (String like : tempLikes) {
                writerLikes.println(like);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        tempLikes.clear();
    }

    /* lista postari peroane urmarite  */
    public void followedListPostDate(String username) {
        ArrayList<String> temp = new ArrayList<>();
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

                                temp.add("{'post_id':'" + postParts[0] + "', 'post_text' : '" + postParts[2]
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
        temp.sort(Collections.reverseOrder());

        if (!temp.isEmpty()) {
            System.out.print("{ 'status' : 'ok', 'message' : [");
            for (int i = 0; i < temp.size(); i++) {
                if (i != 0) {
                    System.out.print(",");
                }
                System.out.print(temp.get(i));
            }
            System.out.print("]}");
        }
    }

    public void UserListPost(String username) {
        ArrayList<String> temp = new ArrayList<>();

        try (BufferedReader postReader = new BufferedReader(new FileReader("postare.csv"))) {
            String postLine;
            while ((postLine = postReader.readLine()) != null) {
                String[] postParts = postLine.split(",");
                if (username.equals(postParts[1])) {
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date date = new Date();
                    String currentDateAsString = dateFormat.format(date);

                    temp.add("{'post_id':'" + postParts[0] + "', 'post_text' : '" + postParts[2]
                            + "', 'post_date' : '" + currentDateAsString + "'}");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // sorteaza, pe urma inverseaza pentru a fi descrescator
        Collections.sort(temp, Collections.reverseOrder());

        if (!temp.isEmpty()) {
            System.out.print("{ 'status' : 'ok', 'message' : [");
            for (int i = 0; i < temp.size(); i++) {
                if (i != 0) {
                    System.out.print(",");
                }
                System.out.print(temp.get(i));
            }
            System.out.print("]}");
        }
    }

    public void detaliiPostare(int idPost) {
        boolean aux = true;
        File file = new File("postare.csv");
        if (file.length() == 0) {
            System.out.println("{'status':'error','message':'The post identifier was not valid'}");
            return;
        }
        try (BufferedReader readerLikes = new BufferedReader(new FileReader("postare.csv"))) {
            String line;
            while ((line = readerLikes.readLine()) != null) {
                String[] parts = line.split(",");
                int currentPostId = Integer.parseInt(parts[0]);
                if (currentPostId == idPost) {

                    try (BufferedReader postReader = new BufferedReader(new FileReader("comentariu.csv"))) {
                        String postLine;
                        while ((postLine = postReader.readLine()) != null) {
                            String[] postParts = postLine.split(",");
                            int currentPostId2 = Integer.parseInt(parts[0]);
                            if (currentPostId2 == idPost) {
                                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                Date date = new Date();
                                String currentDateAsString = dateFormat.format(date);
                                if (aux) {
                                    System.out.print("{ 'status' : 'ok', 'message' : [");
                                    aux = false;
                                } else {
                                    System.out.print(",");
                                }
                                System.out.print("{'post_text':'" + parts[2] + "', 'post_date' : '" + currentDateAsString + "', 'username' : '" + parts[1] +
                                        "', 'number_of_likes' :'" + parts[3] + "', 'comments' : [{'comment_id' : '" + postParts[3] + "' ,'comment_text' : '" +
                                        postParts[4] + "', 'comment_date' : '" + currentDateAsString + "', 'username' : '" + postParts[1] + "', 'number_of_likes' : '" +
                                        postParts[5] + "'}] }]");

                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("{'status':'error','message':'The post identifier was not valid'}");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!aux) {
            System.out.println(" }");
        }
    }


}
