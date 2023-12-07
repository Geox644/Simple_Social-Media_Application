package TemaTest;

import java.io.*;
import java.util.ArrayList;

public class Comentariu {
    String text;
    private final int id;
    private final int idPostare;
    private static int idCount = 1;
    private final String user, password;
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
            System.out.println("Clean");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean textLength() {
        return text.length() <= 300;
    }

    // scriu comentariile in fiser
    public void writeToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("comentariu.csv", true))) {
            writer.write(idPostare + "," + user + "," + password + "," + id + "," + text + "," + nrLikes + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteCommByIdFromFile(int postId, String username, String password) {
        ArrayList<String> tempComm = new ArrayList<>();
        boolean commFound = false;

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
                    tempComm.add(line);
                } else {
                    commFound = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!commFound) {
            System.out.println("{'status':'error','message':'The identifier was not valid'}");
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter("comentariu.csv"))) {
            for (String comm : tempComm) {
                writer.println(comm);
            }
            System.out.println("{'status':'ok','message':'Operation executed successfully'}");
        } catch (IOException e) {
            e.printStackTrace();
        }
        tempComm.clear();
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
            for (String comm : tempComm) {
                writer.println(comm);
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

    public void unlike(int commId, String username) {
        ArrayList<String> tempComm = new ArrayList<>();
        ArrayList<String> tempLikes = new ArrayList<>();
        boolean commFound = false;

        try (BufferedReader readerLikes = new BufferedReader(new FileReader("likeComentariu.csv"))) {
            String lineLike;
            while ((lineLike = readerLikes.readLine()) != null) {
                String[] parts = lineLike.split(",");
                int currentCommId = Integer.parseInt(parts[0]);
                if (currentCommId == commId && username.equals(parts[1])) {
                    int currentNrLikes = Integer.parseInt(parts[2]);
                    currentNrLikes--; // scadere nrLikes
                    this.nrLikes = currentNrLikes;
                    lineLike = currentCommId + "," + parts[1] + "," + currentNrLikes;
                    commFound = true;
                }
                tempLikes.add(lineLike);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader readerComm = new BufferedReader(new FileReader("comentariu.csv"))) {
            String lineComm;
            while ((lineComm = readerComm.readLine()) != null) {
                String[] partsComm = lineComm.split(",");
                int currentCommId = Integer.parseInt(partsComm[0]);
                if (currentCommId == commId) {
                    int currentNrLikes = Integer.parseInt(partsComm[5]);
                    currentNrLikes--; // scadere nrLikes
                    lineComm = currentCommId + "," + partsComm[1] + "," + partsComm[2] + "," + partsComm[3] + "," + partsComm[4] + "," + currentNrLikes; // actualizare linie
                }
                tempComm.add(lineComm);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!commFound) {
            System.out.println("{'status':'error','message':'The comment identifier to unlike was not valid'}");
            tempComm.clear();
            tempLikes.clear();
            return;
        } else {
            System.out.println("{'status':'ok','message':'Operation executed successfully'}");
        }

        try (PrintWriter writerComm = new PrintWriter(new FileWriter("comentariu.csv"))) {
            for (String comm : tempComm) {
                writerComm.println(comm);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        tempComm.clear();

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
