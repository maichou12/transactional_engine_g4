# 🔧 Configurer IntelliJ pour lancer en PROD

## Problème

L'erreur `Failed to load driver class com.mysql.cj.jdbc.Driver` indique que le driver MySQL n'est pas chargé car le profil Maven `prod` n'est pas activé.

## ✅ Solution 1 : Activer le profil Maven dans IntelliJ

### Étapes :

1. **Ouvrez les paramètres Maven** :

   - File → Settings (ou Ctrl+Alt+S)
   - Maven → Runner
   - OU directement : View → Tool Windows → Maven

2. **Activez le profil prod** :

   - Dans la fenêtre Maven (à droite), cliquez sur l'icône "Maven" (ou View → Tool Windows → Maven)
   - Cliquez sur le projet → Profiles
   - Cochez la case **`prod`**

3. **Rechargez Maven** :

   - Cliquez sur l'icône "Reload All Maven Projects" (flèche circulaire)
   - OU : Clic droit sur le projet → Maven → Reload project

4. **Configurez la Run Configuration** :

   - Run → Edit Configurations
   - Dans "Active profiles" : `prod,api-docs`
   - Cliquez Apply puis OK

5. **Lancez l'application**

---

## ✅ Solution 2 : Ajouter MySQL dans les dépendances principales (Plus simple)

Si vous voulez éviter de gérer les profils Maven, ajoutez MySQL dans les dépendances principales du `pom.xml` :

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>
```

Puis rechargez Maven dans IntelliJ.

---

## ✅ Solution 3 : Lancer avec Maven directement

Dans le terminal IntelliJ :

```bash
./mvnw spring-boot:run -Pprod -Dspring-boot.run.profiles=prod,api-docs
```

---

## 🔍 Vérifier que le driver est chargé

Après avoir activé le profil, vérifiez dans les logs au démarrage qu'il n'y a plus l'erreur du driver.

Vous devriez voir :

```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```

Au lieu de :

```
Failed to load driver class com.mysql.cj.jdbc.Driver
```
