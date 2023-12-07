package TemaTest;

import java.io.*;
import java.util.ArrayList;

public class Utilizator {
    private String username;
    private String password;
    private boolean logged;

    public Utilizator(String username, String password) {
        this.username = username;
        this.password = password;
        this.logged = false;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String toString() {
        return username + "," + password;
    }

    public void createUser(String username, String password) {
        if (userExists(username)) {
            System.out.println("{'status':'error','message':'User already exists'}");
        } else {
            writeToFile();
            System.out.println("{'status':'ok','message':'User created successfully'}");
        }
    }

    private boolean userExists(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(username)) {
                    return true; // user-ul exista
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkLogin(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 1 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true; // parola este corecta
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*  SCRIERE IN FISIER  */

    public void writeToFile() {
        FileWriter userFile;
        try {
            userFile = new FileWriter("users.csv", true);

            BufferedWriter userWrite = new BufferedWriter(userFile);
            userFile.write(this.toString() + "\n");

            userWrite.close();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    public boolean login() {
        if (checkLogin(username, password)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean followUser(String followedUsername) {

        if (userExists(followedUsername)) {
            if (isAlreadyFollowing(followedUsername)) {
                System.out.println("{'status':'error','message':'The username to follow was not valid'}");
                return false;
            } else {
                System.out.println("{'status':'ok','message':'Operation executed successfully'}");
                writeFollowToFile(followedUsername);
                return true;
            }
        } else {
            System.out.println("{'status':'error','message':'The username to follow was not valid'}");
            return false;
        }
    }

    private void writeFollowToFile(String followedUsername) {
        try (FileWriter followFile = new FileWriter("follows.csv", true)) {
            BufferedWriter followWrite = new BufferedWriter(followFile);
            followFile.write(this.username + "," + followedUsername + "\n");
            followWrite.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean isAlreadyFollowing(String followedUsername) {
        try (BufferedReader reader = new BufferedReader(new FileReader("follows.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 1 && parts[0].equals(username) && parts[1].equals(followedUsername)) {
                    return true; // User is already following
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean unfollowUser(String unfollowedUsername) {
        if (userExists(unfollowedUsername)) {
            // Verifica daca utilizatorul curent a dat deja unfollow
            if (isAlreadyUnfollowed(unfollowedUsername)) {
                System.out.println("{'status':'error','message':'The username to unfollow was not valid'}");
                return false;
            }
            // Verifica daca utilizatorul curent urmareste deja utilizatorul specificat
            if (isAlreadyFollowing(unfollowedUsername)) {
                // Elimina relatia de follow din fisierul follows.csv
                removeFollowFromFile(unfollowedUsername);
                System.out.println("{'status':'ok','message':'Operation executed successfully'}");
                return true;
            } else {
                System.out.println("{'status':'error','message':'You are not following this user'}");
                return false;
            }
        } else {
            System.out.println("{'status':'error','message':'The username to unfollow was not valid'}");
            return false;
        }
    }

    private void removeFollowFromFile(String unfollowedUsername) {
        // Creeaza o lista temporara pentru a retine relatiile de follow
        ArrayList<String> tempFollows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("follows.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 1 && parts[0].equals(username) && parts[1].equals(unfollowedUsername)) {

                } else {

                    tempFollows.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Suprascrie fisierul follows.csv cu lista actualizata
        try (PrintWriter followWriter = new PrintWriter(new FileWriter("follows.csv"))) {
            for (String follow : tempFollows) {
                followWriter.println(follow);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isAlreadyUnfollowed(String unfollowedUsername) {
        try (BufferedReader reader = new BufferedReader(new FileReader("follows.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 1 && parts[0].equals(username) && parts[1].equals(unfollowedUsername)) {
                    return false; // Utilizatorul curent nu a dat inca unfollow
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true; // Utilizatorul curent a dat deja unfollow
    }

    public void listaPersoaneUrmarite(String username) {
        boolean aux = true;
        try (BufferedReader reader = new BufferedReader(new FileReader("follows.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (username.equals(parts[0]) ) {

                    if (aux) {
                        System.out.print("{ 'status' : 'ok', 'message' : [");
                        aux = false;
                    } else {
                        System.out.print(",");
                    }

                    System.out.print("'" + parts[1] + "'");
                }
                }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!aux) {
            System.out.print("]}");
        }
    }


    public void listaUrmaritori(String username) {
        boolean aux = true;
        if (!userExists(username)) {
            System.out.println("{'status':'error','message':'The username to list followers was not valid'}");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader("follows.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (username.equals(parts[1]) ) {

                    if (aux) {
                        System.out.print("{ 'status' : 'ok', 'message' : [");
                        aux = false;
                    } else {
                        System.out.print(",");
                    }

                    System.out.print("'" + parts[0] + "'");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!aux) {
            System.out.print("]}");
        }
    }

    public void topCinciCeiMaiUrmaritiUsers(String username) {
        boolean aux = true;
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("follows.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (username.equals(parts[1]) ) {
                    count++;
                    if (aux) {
                        System.out.print("{ 'status' : 'ok', 'message' : [");
                        aux = false;
                    } else {
                        System.out.print(",");
                    }

                    System.out.print("'" + parts[0] + "'");
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
