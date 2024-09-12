import firebase from 'firebase/compat/app';
import 'firebase/compat/database';

const firebaseConfig = {
  apiKey: 'AIzaSyDA19hOUUIdUsOMiNbb_W41v9yKaqR1fek',
  authDomain: 'lms-chats.firebaseapp.com',
  databaseURL:
    'https://lms-chats-default-rtdb.asia-southeast1.firebasedatabase.app',
  projectId: 'lms-chats',
  storageBucket: 'lms-chats.appspot.com',
  messagingSenderId: '74220990475',
  appId: '1:74220990475:web:a2e33d60cbba6a85e8570e',
  measurementId: 'G-79FJV88W0D',
};

firebase.initializeApp(firebaseConfig);

const database = firebase.database();
const databaseRef = database.ref('/busStation');

export default databaseRef;
