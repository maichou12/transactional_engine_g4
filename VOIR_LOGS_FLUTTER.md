# 📱 Comment Voir les Logs Flutter

## Problème

Vous ne voyez aucun log dans la console, même après avoir cliqué sur le bouton.

## Solutions

### 1. Dans VS Code

1. **Ouvrez le terminal intégré** (Terminal → New Terminal)
2. **Lancez l'app en mode debug** :
   ```bash
   flutter run
   ```
3. **OU utilisez la commande avec logs détaillés** :
   ```bash
   flutter run -v
   ```
4. **Les logs apparaîtront dans le terminal**

### 2. Dans Android Studio

1. **Ouvrez la vue "Run"** en bas de l'écran
2. **OU ouvrez "Logcat"** (View → Tool Windows → Logcat)
3. **Filtrez les logs** :
   - Dans Logcat, utilisez le filtre : `flutter`
   - OU cherchez : `I/flutter` ou `D/flutter`

### 3. Via ADB (Android Debug Bridge)

Si vous utilisez un émulateur ou un appareil physique :

```bash
# Voir tous les logs Flutter
adb logcat | grep flutter

# Voir uniquement les logs de votre app
adb logcat | grep "frontend_transactional_engine"

# Voir les logs avec nos marqueurs
adb logcat | grep -E "\[FRONTEND\]|\[API\]"
```

### 4. Activer les Logs Détaillés

Dans votre terminal, lancez :

```bash
# Mode verbose (très détaillé)
flutter run -v

# OU avec logs spécifiques
flutter run --verbose
```

### 5. Vérifier que les Logs sont Activés

Dans VS Code, vérifiez que vous êtes en mode **Debug** :

- Appuyez sur `F5` pour lancer en mode debug
- OU cliquez sur l'icône "Run and Debug" dans la barre latérale

### 6. Test Rapide - Ajouter un Log au Démarrage

Pour vérifier que les logs fonctionnent, ajoutez ceci dans `main.dart` :

```dart
void main() {
  print('🚀 [APP] Application démarrée !');
  runApp(MyApp());
}
```

Si vous voyez ce log au démarrage, les logs fonctionnent.

### 7. Utiliser `debugPrint` au lieu de `print`

Parfois `print` ne s'affiche pas. Utilisez `debugPrint` :

```dart
import 'package:flutter/foundation.dart';

debugPrint('🔵 [API] Ce log sera toujours visible');
```

### 8. Vérifier la Console Flutter DevTools

1. **Lancez l'app** : `flutter run`
2. **Ouvrez DevTools** : Appuyez sur `d` dans le terminal
3. **OU** ouvrez dans le navigateur : `http://localhost:9100`
4. **Allez dans l'onglet "Logging"**

## 🔍 Commandes Utiles

```bash
# Voir les logs en temps réel
flutter logs

# Voir les logs avec filtres
adb logcat *:S flutter:V

# Nettoyer les logs et recommencer
adb logcat -c
flutter logs
```

## ✅ Checklist

- [ ] L'app est lancée en mode debug (F5 dans VS Code)
- [ ] Le terminal est ouvert et visible
- [ ] Vous avez cliqué sur le bouton "S'inscrire"
- [ ] Vous cherchez les logs avec `[FRONTEND]` ou `[API]`
- [ ] Vous avez testé avec `flutter run -v`

## 🎯 Test Immédiat

1. **Ouvrez un terminal**
2. **Tapez** :
   ```bash
   flutter run
   ```
3. **Dans l'app, cliquez sur "S'inscrire"**
4. **Regardez le terminal** - vous devriez voir :
   ```
   ═══════════════════════════════════════════════════════════
   🔵 [FRONTEND] Bouton "S'inscrire" cliqué !
   🔵 [FRONTEND] Texte du champ téléphone: "771234578"
   ...
   ```

Si vous ne voyez **RIEN** après avoir cliqué, c'est que :

- Le bouton n'est pas cliqué (vérifiez visuellement)
- L'app n'est pas en mode debug
- Les logs sont filtrés ou cachés
