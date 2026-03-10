import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { register } from '../api/authService';
import { Eye, EyeOff } from 'lucide-react';

export const RegisterPage = () => {
    const [formData, setFormData] = useState({ username: '', email: '', password: '' });
    const navigate = useNavigate();
    const [showPassword, setShowPassword] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        console.log(formData);
        try {
            await register(formData);
            navigate('/login');
        } catch (err) {
            alert('Registracija nije uspela.');
        }
    };

    return (
        <div className="max-w-md mx-auto mt-16 p-8 border rounded-xl bg-white shadow-lg">
            <h2 className="text-3xl font-bold mb-8 text-center text-gray-900">Registracija</h2>

            <form onSubmit={handleSubmit} className="space-y-5">
                <input
                    type="text"
                    name="username"
                    value={formData.username}
                    autoComplete="username"
                    placeholder="korisnicko ime"
                    className="w-full border-gray-300 rounded-lg p-3 border outline-none focus:border-black"
                    onChange={(e) => setFormData({...formData, username: e.target.value})}
                    required
                />
                <input
                    type="email"
                    placeholder="Email"
                    className="w-full border-gray-300 rounded-lg p-3 border outline-none focus:border-black"
                    onChange={(e) => setFormData({...formData, email: e.target.value})}
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
                            className="w-full border-gray-300 rounded-lg p-3 border outline-none focus:border-black transition-all"
                            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
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
                    Napravi nalog
                </button>
            </form>

            <div className="mt-8 pt-6 border-t border-gray-100 text-center text-sm text-gray-600">
                Već imate nalog?{' '}
                <Link to="/login" className="text-black font-bold hover:underline">
                    Prijavite se
                </Link>
            </div>
        </div>
    );
};