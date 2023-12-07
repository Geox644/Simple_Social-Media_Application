package TemaTest;

import java.io.*;
import java.util.ArrayList;

public class Utilizator {
    private final String username;
    private final String password;

    public Utilizator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void createUser(String username) {
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
                    return true; // si parola este corecta
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
            userFile.write(username + "," + password + "\n");

            userWrite.close();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkLogin() {
        return !checkLogin(username, password);
    }

    public void followUser(String followedUsername) {

        if (userExists(followedUsername)) {
            if (isAlreadyFollowing(followedUsername)) {
                System.out.println("{'status':'error','message':'The username to follow was not valid'}");
            } else {
                System.out.println("{'status':'ok','message':'Operation executed successfully'}");
                writeFollowToFile(followedUsername);
            }
        } else {
            System.out.println("{'status':'error','message':'The username to follow was not valid'}");
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
                    return true; // deja il urmareste
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean unfollowUser(String unfollowedUsername) {
        if (userExists(unfollowedUsername)) {
            // verifica daca i-a dat deja unfollow
            if (isAlreadyUnfollowed(unfollowedUsername)) {
                System.out.println("{'status':'error','message':'The username to unfollow was not valid'}");
                return false;
            }
            // verifica daca il urmareste ca sa-i poata da unfollow
            if (isAlreadyFollowing(unfollowedUsername)) {
                removeFollowFromFile(unfollowedUsername); // il sterge din fiser
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
        ArrayList<String> temp = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("follows.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (!parts[0].equals(username) && !parts[1].equals(unfollowedUsername)) {
                    temp.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // suprascriu fiserul cu datele din arraylist
        try (PrintWriter followWriter = new PrintWriter(new FileWriter("follows.csv"))) {
            for (String follow : temp) {
                followWriter.println(follow);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isAlreadyUnfollowed(String unfollowedUsername) {
        try (BufferedReader reader = new BufferedReader(new FileReader("follows.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username) && parts[1].equals(unfollowedUsername)) {
                    return false; // nu il urmareste
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true; // il urmareste
    }

    public void listaPersoaneUrmarite(String username) {
        boolean aux = true;
        try (BufferedReader reader = new BufferedReader(new FileReader("follows.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (username.equals(parts[0])) {

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
                if (username.equals(parts[1])) {

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


    public void topCinciCeiMaiUrmaritiUsers() {
        String[] users = new String[20];
        int[] counts = new int[20];

        try (BufferedReader reader = new BufferedReader(new FileReader("follows.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String followed = parts[1];

                int index = findUser(users, followed);

                // daca user exista, incrementez numarul de aparitia user-ului
                if (index != -1) {
                    counts[index]++;
                } else {
                    int emptyIndex = findEmptyIndex(users);
                    users[emptyIndex] = followed;
                    counts[emptyIndex] = 1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // sortez descrescator ca sa iau nr de aparitii cel mai mare
        sortArrays(users, counts);

        // afisez primii 5
        System.out.print("{ 'status' : 'ok', 'message' : [");
        boolean aux = false;
        for (int i = 0; i < 5; i++) {
            if (users[i] != null) {
                if (aux) {
                    System.out.print(",");
                }
                System.out.print("{'username': '" + users[i] + "', 'number_of_followers': '" + counts[i] + "'}");
                aux = true;
            }
        }
        System.out.println(" ]}");
    }

    // functie care cauta daca deja user-ul a fost deja gasit pentru a putea fi numarat
    private static int findUser(String[] users, String user) {
        for (int i = 0; i < users.length; i++) {
            if (user != null && user.equals(users[i])) {
                return i;
            }
        }
        return -1;
    }

    // daca user-ul nu a fost deja gasit il adaug intr-o noua pozitie in vector
    private static int findEmptyIndex(String[] users) {
        for (int i = 0; i < users.length; i++) {
            if (users[i] == null) {
                return i;
            }
        }
        return -1;
    }

    // sortare descrescatoare
    private static void sortArrays(String[] users, int[] counts) {
        for (int i = 0; i < users.length - 1; i++) {
            for (int j = i + 1; j < users.length; j++) {
                if (counts[i] < counts[j]) {
                    String tempUser = users[i];
                    int tempCount = counts[i];

                    users[i] = users[j];
                    counts[i] = counts[j];

                    users[j] = tempUser;
                    counts[j] = tempCount;
                }
            }
        }
    }

}
