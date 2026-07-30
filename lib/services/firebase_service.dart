import 'package:cloud_firestore/cloud_firestore.dart';
import '../models/user_model.dart';

class FirebaseService {
  final FirebaseFirestore _db = FirebaseFirestore.instance;

  Future<UserModel?> login(String email, String password) async {
    try {
      final querySnapshot = await _db
          .collection('users')
          .where('email', isEqualTo: email)
          .where('password', isEqualTo: password)
          .get();

      if (querySnapshot.docs.isNotEmpty) {
        return UserModel.fromMap(querySnapshot.docs.first.data());
      }
      return null;
    } catch (e) {
      rethrow;
    }
  }

  Future<void> register(UserModel user, String password) async {
    try {
      final userData = user.toMap();
      userData['password'] = password; // Matches existing logic
      await _db.collection('users').doc(user.id).set(userData);
    } catch (e) {
      rethrow;
    }
  }

  Stream<List<Map<String, dynamic>>> getItems() {
    // Porting the 'PlaceholderContent' logic or similar
    return _db.collection('items').snapshots().map((snapshot) {
      return snapshot.docs.map((doc) => doc.data()).toList();
    });
  }
}
