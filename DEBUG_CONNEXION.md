# 🔍 Guide de Débogage - Aucune Requête n'Arrive au Backend

## Problème

Aucun log n'apparaît dans le backend, ce qui signifie que la requête n'arrive pas au serveur.

## ✅ Checklist de Vérification

### 1. Vérifier que le Backend est Accessible

#### Test depuis votre navigateur (sur la même machine) :

```
http://localhost:9089/swagger-ui.html
```

**Si ça ne fonctionne pas** → Le backend n'est pas démarré correctement.

#### Test depuis l'émulateur Android (via adb) :

```bash
adb reverse tcp:9089 tcp:9089
```

Puis dans le frontend, utilisez : `http://localhost:9089`

**OU** utilisez directement : `http://10.0.2.2:9089` (déjà configuré ✅)

### 2. Vérifier les Logs Flutter

Dans votre console Flutter (VS Code / Android Studio), vous devriez voir :

#### ✅ Si la requête est envoyée :

```
📱 [FRONTEND] Numéro formaté avant envoi: +221771234578
🔵 [API] Appel inscriptionEtape1
🔵 [API] URL: http://10.0.2.2:9089/api/auth/inscription/etape1
🔵 [API] Body: {"telephone":"+221771234578"}
```

#### ❌ Si vous voyez une erreur :

```
❌ [API] Exception: ...
❌ [API] ClientException: ...
❌ [API] Timeout: ...
```

### 3. Vérifier la Configuration du Backend

Le backend doit écouter sur **toutes les interfaces** (0.0.0.0), pas seulement localhost.

Vérifiez dans `application-dev.yml` :

```yaml
server:
  port: 9089
  address: 0.0.0.0 # ← Ajoutez cette ligne si elle n'existe pas
```

### 4. Test avec ADB Reverse (Solution Alternative)

Si `10.0.2.2` ne fonctionne pas, utilisez `adb reverse` :

```bash
# Dans votre terminal
adb reverse tcp:9089 tcp:9089
```

Puis dans `api_service.dart`, changez :

```dart
static const String baseUrl = 'http://localhost:9089';
```

### 5. Vérifier le Firewall Windows

Le firewall Windows peut bloquer les connexions entrantes.

1. Ouvrez **Pare-feu Windows Defender**
2. Cliquez sur **Paramètres avancés**
3. Vérifiez que le port 9089 n'est pas bloqué

**Solution rapide** : Désactivez temporairement le firewall pour tester.

### 6. Test Direct avec curl

Testez si le backend répond depuis votre machine :

```bash
curl -X POST http://localhost:9089/api/auth/inscription/etape1 \
  -H "Content-Type: application/json" \
  -d '{"telephone":"+221771234567"}'
```

**Si ça fonctionne** → Le backend est OK, le problème vient de la connexion frontend-backend.

**Si ça ne fonctionne pas** → Le backend a un problème.

### 7. Vérifier les Logs Flutter en Détail

Ajoutez plus de logs dans `api_service.dart` pour voir exactement ce qui se passe :

```dart
static Future<Map<String, dynamic>> inscriptionEtape1(String telephone) async {
  try {
    final url = '$baseUrl/api/auth/inscription/etape1';
    final body = jsonEncode({'telephone': telephone});

    print('═══════════════════════════════════════════════════════════');
    print('🔵 [API] Début de l\'appel inscriptionEtape1');
    print('🔵 [API] URL complète: $url');
    print('🔵 [API] Body: $body');
    print('🔵 [API] Téléphone: $telephone');
    print('═══════════════════════════════════════════════════════════');

    final response = await http.post(
      Uri.parse(url),
      headers: {
        'accept': '*/*',
        'Content-Type': 'application/json',
      },
      body: body,
    ).timeout(
      Duration(seconds: 10),
      onTimeout: () {
        print('❌ [API] TIMEOUT - La requête a pris plus de 10 secondes');
        throw Exception('Timeout: La requête a pris trop de temps');
      },
    );

    print('🔵 [API] Réponse reçue !');
    print('🔵 [API] Status Code: ${response.statusCode}');
    print('🔵 [API] Response Body: ${response.body}');
    // ... reste du code
  } catch (e, stackTrace) {
    print('❌ [API] ERREUR DÉTAILLÉE');
    print('❌ [API] Type: ${e.runtimeType}');
    print('❌ [API] Message: ${e.toString()}');
    print('❌ [API] StackTrace: $stackTrace');
    return {'success': false, 'error': 'Erreur: ${e.toString()}'};
  }
}
```

### 8. Solution Rapide : Utiliser ADB Reverse

C'est souvent la solution la plus simple :

1. **Connectez votre émulateur Android**
2. **Dans un terminal, exécutez :**
   ```bash
   adb reverse tcp:9089 tcp:9089
   ```
3. **Dans `api_service.dart`, changez :**
   ```dart
   static const String baseUrl = 'http://localhost:9089';
   ```
4. **Redémarrez l'app Flutter**

### 9. Vérifier que le Backend Écoute sur Toutes les Interfaces

Vérifiez dans les logs du backend au démarrage :

```
Tomcat started on port(s): 9089 (http)
```

Si vous voyez quelque chose comme :

```
Tomcat started on port(s): 9089 (http) with context path ''
```

C'est bon. Si vous voyez `localhost` ou `127.0.0.1`, c'est un problème.

### 10. Test avec Postman / Swagger

Testez l'endpoint directement depuis Swagger :

1. Ouvrez : `http://localhost:9089/swagger-ui.html`
2. Testez l'endpoint `/api/auth/inscription/etape1`
3. Si ça fonctionne → Le backend est OK
4. Si ça ne fonctionne pas → Problème backend

## 🎯 Solution Recommandée (Ordre de Priorité)

1. **Utilisez ADB Reverse** (le plus simple)
2. **Vérifiez les logs Flutter** pour voir l'erreur exacte
3. **Vérifiez que le backend écoute sur 0.0.0.0**
4. **Testez avec curl** pour confirmer que le backend fonctionne

## 📝 Commandes Utiles

```bash
# Vérifier que le port 9089 est utilisé
netstat -ano | findstr :9089

# Vérifier les connexions ADB
adb devices

# Créer le reverse proxy ADB
adb reverse tcp:9089 tcp:9089

# Vérifier que le reverse est actif
adb reverse --list
```
