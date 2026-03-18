import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom'; // Import Link here
import { login } from '../api/authService';
import {Eye, EyeOff} from "lucide-react";

export const LoginPage = () => {
    const [username, setusername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState<string | null>(null);
    const [showPassword, setShowPassword] = useState(false);
    const navigate = useNavigate();

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        console.log(password);
        console.log(username);
        try {
            const response = await login({ username, password });
            localStorage.setItem('token', response.token);
            navigate('/');
            window.location.reload();
        } catch (err: any) {
            setError('Pogrešan username ili lozinka.');
        }
    };

    return (
        <div className="max-w-md mx-auto mt-16 p-8 border rounded-xl bg-white shadow-lg">
            <h2 className="text-3xl font-bold mb-8 text-center text-gray-900">Prijava</h2>

            {error && <div className="text-red-500 mb-4 text-sm text-center">{error}</div>}

            <form onSubmit={handleLogin} className="space-y-5">
                <input
                    type="text"
                    placeholder="username"
                    className="w-full border-gray-300 rounded-lg p-3 border outline-none focus:border-black"
                    value={username}
                    onChange={(e) => setusername(e.target.value)}
                    required
                />
                <div className="flex flex-col gap-1">
                    <div className="flex justify-end w-full px-1">
                        <button
                            type="button"
                            onClick={() => setShowPassword(!showPassword)}
                            className="text-gray-500 hover:text-black transition-colors focus:outline-none"
                            title={showPassword ? "Sakrij lozinku" : "Prikaži lozinku"}
                        >
                            {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                        </button>
                    </div>

                    <div className="relative">
                        <input
                            type={showPassword ? "text" : "password"}
                            placeholder="Lozinka"
                            className="w-full border-gray-300 rounded-lg p-3 border outline-none focus:border-black"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />

                        <style>{`
            input::-ms-reveal,
            input::-ms-clear,
            input::-webkit-contacts-auto-fill-button,
            input::-webkit-credentials-auto-fill-button {
                display: none !important;
            }
        `}</style>
                    </div>
                </div>
                <button className="w-full bg-black text-white py-3 rounded-lg font-bold hover:bg-gray-800 transition">
                    Prijavi se
                </button>
            </form>

            <div className="mt-8 pt-6 border-t border-gray-100 text-center text-sm text-gray-600">
                Nemate nalog?{' '}
                <Link to="/register" className="text-black font-bold hover:underline">
                    Registrujte se
                </Link>
            </div>
        </div>
    );
};