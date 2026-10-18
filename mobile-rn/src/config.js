import { Platform } from 'react-native';

// Android emulators reach the host machine at 10.0.2.2; iOS simulators use localhost.
// For a physical phone, set EXPO_PUBLIC_API_URL to your computer's LAN IP,
// e.g. EXPO_PUBLIC_API_URL=http://192.168.1.20:8080 npx expo start
const defaultHost = Platform.OS === 'android' ? 'http://10.0.2.2:8080' : 'http://localhost:8080';

export const API_BASE_URL = process.env.EXPO_PUBLIC_API_URL || defaultHost;
