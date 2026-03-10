import { ProductList } from './features/products/components/ProductList';

function App() {
    return (
        <div className="min-h-screen bg-gray-50">
            <header className="bg-white shadow-sm p-4 mb-6">
                <h1 className="text-2xl font-bold text-center text-blue-600">My Webshop</h1>
            </header>
            <main>
                <ProductList />
            </main>
        </div>
    );
}

export default App;