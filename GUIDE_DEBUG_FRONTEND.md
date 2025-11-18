# Guide de Débogage Frontend - Problèmes de Connexion API

## Problèmes Identifiés et Solutions

### 1. ✅ CORS Activé dans le Backend

Le CORS a été activé dans `application-dev.yml` pour permettre les appels depuis le frontend mobile.

**Redémarrez le backend** pour que les changements prennent effet.

### 2. ⚠️ URL du Backend dans le Frontend

Le problème principal est que le frontend utilise `http://localhost:9089`, ce qui **ne fonctionne pas** depuis :

- Un appareil mobile physique
- Un émulateur Android
- Un émulateur iOS

#### Solution pour Android Emulator :

Dans `lib/services/api_service.dart`, changez :

```dart
static const String baseUrl = 'http://localhost:9089';
```

Par :

```dart
static const String baseUrl = 'http://10.0.2.2:9089';  // Pour Android Emulator
```

#### Solution pour iOS Simulator :

```dart
static const String baseUrl = 'http://localhost:9089';  // Fonctionne pour iOS Simulator
```

#### Solution pour Appareil Physique :

Vous devez utiliser l'**adresse IP de votre machine** sur le réseau local :

1. **Trouver votre adresse IP :**

   - Windows : Ouvrez PowerShell et tapez `ipconfig`
   - Cherchez "IPv4 Address" (ex: `192.168.1.100`)
   - Mac/Linux : `ifconfig` ou `ip addr`

2. **Modifier l'URL dans `api_service.dart` :**

   ```dart
   static const String baseUrl = 'http://192.168.1.100:9089';  // Remplacez par votre IP
   ```

3. **Vérifier que le backend est accessible :**
   - Depuis votre navigateur sur la machine, testez : `http://192.168.1.100:9089/swagger-ui.html`
   - Depuis votre téléphone sur le même réseau WiFi, testez la même URL

### 3. 🔍 Vérifier les Logs

Le code frontend a déjà des logs de débogage. Vérifiez dans la console Flutter :

#### Dans VS Code / Android Studio :

- Ouvrez la console de débogage
- Cherchez les logs commençant par `🔵 [API]` ou `❌ [API]`

#### Exemple de logs attendus :

```
🔵 [API] Appel inscriptionEtape1
🔵 [API] URL: http://10.0.2.2:9089/api/auth/inscription/etape1
🔵 [API] Body: {"telephone":"+221771234567"}
🔵 [API] Status Code: 200
✅ [API] Code OTP envoyé avec succès
```

#### Si vous voyez des erreurs :

- `ClientException` : Le backend n'est pas accessible (vérifiez l'URL et que le backend est démarré)
- `Timeout` : Le backend met trop de temps à répondre
- `400 Bad Request` : Vérifiez le format des données envoyées
- `CORS error` : Le CORS n'est pas correctement configuré

### 4. 📱 Configuration Recommandée pour le Développement

Créez un fichier de configuration pour faciliter le changement d'environnement :

**Créer `lib/config/app_config.dart` :**

```dart
class AppConfig {
  // Pour Android Emulator
  static const String baseUrlAndroid = 'http://10.0.2.2:9089';

  // Pour iOS Simulator
  static const String baseUrlIOS = 'http://localhost:9089';

  // Pour appareil physique (remplacez par votre IP)
  static const String baseUrlPhysical = 'http://192.168.1.100:9089';

  // Sélectionner l'URL selon la plateforme
  static String get baseUrl {
    // Vous pouvez détecter la plateforme ici
    // ou utiliser une variable d'environnement
    return baseUrlAndroid; // Par défaut pour Android
  }
}
```

**Modifier `lib/services/api_service.dart` :**

```dart
import '../config/app_config.dart';

class ApiService {
  static String get baseUrl => AppConfig.baseUrl;
  // ... reste du code
}
```

### 5. ✅ Checklist de Vérification

Avant de tester, vérifiez :

- [ ] Le backend est démarré et accessible sur le port 9089
- [ ] CORS est activé dans `application-dev.yml` (déjà fait ✅)
- [ ] L'URL dans `api_service.dart` est correcte pour votre plateforme
- [ ] Le backend et le frontend sont sur le même réseau (pour appareil physique)
- [ ] Les logs Flutter sont activés dans votre IDE
- [ ] Le numéro de téléphone est au format `+221771234567`

### 6. 🧪 Test Rapide

Pour tester rapidement si le backend est accessible :

1. **Depuis votre navigateur** (sur la même machine que le backend) :

   ```
   http://localhost:9089/swagger-ui.html
   ```

2. **Depuis votre navigateur** (sur un autre appareil sur le même réseau) :

   ```
   http://VOTRE_IP:9089/swagger-ui.html
   ```

3. **Testez l'endpoint directement** :
   ```bash
   curl -X POST http://localhost:9089/api/auth/inscription/etape1 \
     -H "Content-Type: application/json" \
     -d '{"telephone":"+221771234567"}'
   ```

### 7. 🐛 Erreurs Courantes

#### "Connection refused" ou "Failed to connect"

- **Cause** : Le backend n'est pas démarré ou l'URL est incorrecte
- **Solution** : Vérifiez que le backend tourne et que l'URL est correcte

#### "CORS policy" error

- **Cause** : CORS n'est pas activé ou mal configuré
- **Solution** : Vérifiez `application-dev.yml` et redémarrez le backend

#### "Timeout"

- **Cause** : Le backend met trop de temps à répondre ou n'est pas accessible
- **Solution** : Vérifiez la connexion réseau et les logs du backend

#### "400 Bad Request"

- **Cause** : Format des données incorrect
- **Solution** : Vérifiez que le numéro de téléphone est au format `+221771234567`

### 8. 📞 Support

Si le problème persiste :

1. Vérifiez les logs du backend (console Spring Boot)
2. Vérifiez les logs du frontend (console Flutter)
3. Testez l'endpoint directement avec Swagger ou Postman
4. Vérifiez que le format du numéro de téléphone est correct
