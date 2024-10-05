import firebase from 'firebase/compat/app';
import 'firebase/compat/database';

const firebaseConfig = {
  apiKey: "AIzaSyAR8fzCWDNCT5wt92ARPzZlC-Ae-Bjozm4",
  authDomain: "bus-station-chat.firebaseapp.com",
  databaseURL: "https://bus-station-chat-default-rtdb.firebaseio.com",
  projectId: "bus-station-chat",
  storageBucket: "bus-station-chat.appspot.com",
  messagingSenderId: "538391703504",
  appId: "1:538391703504:web:927b83a2b76525e046d3d5",
  measurementId: "G-NR12DM5QJE"
};

firebase.initializeApp(firebaseConfig);

const database = firebase.database();
const databaseRef = database.ref('/busStation');

export default databaseRef;
