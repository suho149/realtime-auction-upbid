import { BrowserRouter, Routes, Route } from 'react-router-dom';
import MainPage from './pages/MainPage';
import LoginPage from './pages/LoginPage';
import OAuth2RedirectPage from './pages/OAuth2RedirectPage';

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<MainPage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/oauth2/redirect" element={<OAuth2RedirectPage />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;