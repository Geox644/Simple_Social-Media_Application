Grigoreanu Andreea-Georgiana - 323CB - Tema 1 - Simple Social App

Prezentare generala:
1. In clasa App se afla main-ul, unde am verificat daca comenzile sunt date corect si am afisat mesajele corespunzatoare.
Mai mult decat atat am implementat si o metoda care sa stearga continutul fisierelor, numita "cleanupAll" si
o metoda care sa-mi extraga argumentul comezii date in consola dintre ghilimele simple, numita "extractArgument".
2. In clasa Utilizator am implementat toate metodele necesare care se ocupa cu manipularea utilizatorului.
3. In clasa Postare sunt implementate toate metodele necesare pentru a lucra cu postarile. Mai mult sunt implementate si metode
ce se ocupa de like-unlike pentru o postare, de aceea am implementat clasei interfata Likeable.
4. Clasa Comentariu functioneaza pe acelasi principu ca si clasa Postare.

Intreaga mea implementare se bazeaza pe stocarea informatiilor in fisere.

Clasa Utilizator:


1. userExists(String username) - verifica daca user-ul a fost deja creat
2. checkLogin(String username, String password) - verifica daca exsita in fiser user-ul si parola
3. writeToFile() - scrie in fiser
4. login() - daca exista user-ul si parola inseamna ca astea se poate conecta si intra in cont 
5. followUser(String followedUsername) - ca sa realizez urmariea celor doi, practic scriu numele user-ului care urmareste si dupa user-ul urmarit
6. writeFollowToFile(String followedUsername) - scrie in fiser
7. isAlreadyFollowing(String followedUsername) - verifica daca exista deka in fiser ca sa vada daca user-ul deja il urmareste
8. removeFollowFromFile(String unfollowedUsername) - folosesc un ArrayList care sa-mi retinta doar elementele care nu vreau sa fie sterse. Astfel, atunci cand voi suprascrie fisierul imi va sterge relatiile de urmarire si imi va pune in fisier doar ce a fost stocat temporar in ArrayList 
9. isAlreadyUnfollowed(String unfollowedUsername) - verifica daca user-ul este urmarit sau nu prin citirea elementeor din fiser 
10. listaPersoaneUrmarite(String username) - citeste elementele din fiser si se afiseaza 
11. listaUrmaritori(String username) - analog 10
12. public void topCinciCeiMaiUrmaritiUsers(String username) - caut in fiser numele persoanei urmarite, pe care le pun intr-un vector, la fel si numarul lor. Folosesc functiile findUser si findEmtyIndex ca sa verific daca userul a fost deja gasit si trebuie numarat sau trebuie adugat unul nou in vector. Pe urma le odonez descrescator ca sa i afisez pe primii cinci cei mai urmariti. 


Clasa Postare:

1. clean() - am facut o functie separata de clean pentru a actualiza si id-ul
2. textLength() - verifica ca lungimea textului sa nu fie mai lunga de 300 de caractere
3. writeToFile() - scrie datele specificate in fiser
4. deletePostByIdFromFile(int postId) - sterge din fiser in functie de id-ul specificat. Folosesc un ArrayList ca sa stochez toate datele cu exceptia celei cu id-ul cautat ce trebuie sters. Fac asta pentru a putea suprascrie fisierul, fara postarea cautata.
5. like(int postId, String username) - in mare parte doar incrementez variabila "nrLikes" atunci cand este postarea apreciata, dar metoda este asa lunga deoarece tot actualizez informatiile din fisere
6. userAlreadyLike(String username, int id) - ca sa verific daca deja postarea a fost apreciata am pus intr-un alt fiser postarile apreciate. De aceea, daca gasesc in fisierul likePostare.csv postarea, inseamna ca a fost apreciata deja si nu se mai poate repeta.
7. unlike(int postId, String username) - cam acelasi lucru ca la like, doar ca scad numarul de aprecieri
8. followedListPostDate(String username) - lista persoanelor urmarite, care este sprtata descrescator si dupa id. Ma folosesc tot de un ArrayList temporar
9. UserListPost(String username) - analog 8
10. detaliiPostare(int idPost) - citesc informatiile stocate in fisierul postare.csv si comentariu.csv


Clasa Comentariu este aproape la fel ca si clasa Postare, cu mici ajustari ca sa se potriveasca, dar are aceeasi logica.


