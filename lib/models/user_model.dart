import 'package:cloud_firestore/cloud_firestore.dart';

class UserModel {
  final String id;
  final String nama;
  final String email;
  final String? profilePhoto;
  final DateTime? createdAt;

  UserModel({
    required this.id,
    required this.nama,
    required this.email,
    this.profilePhoto,
    this.createdAt,
  });

  factory UserModel.fromMap(Map<String, dynamic> map) {
    return UserModel(
      id: map['id'] ?? '',
      nama: map['nama'] ?? '',
      email: map['email'] ?? '',
      profilePhoto: map['profile_photo'],
      createdAt: (map['createdAt'] as Timestamp?)?.toDate(),
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'nama': nama,
      'email': email,
      'profile_photo': profilePhoto,
      'createdAt': createdAt ?? FieldValue.serverTimestamp(),
    };
  }
}
