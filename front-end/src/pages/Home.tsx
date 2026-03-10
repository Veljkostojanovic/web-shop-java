import * as React from 'react';
import { useEffect, useState } from 'react';
import { getProducts } from '../features/products/api/getProducts';
import { Product } from '../types/product';

const Home = () => {
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        const fetchItems = async () => {
            try {
                const data = await getProducts();
                setProducts(data);
            } catch (error) {
                console.error("Failed to load products", error);
            } finally {
                setLoading(false);
            }
        };

        fetchItems();
    }, []);

    if (loading) return <div className="loader">Loading our shop...</div>;

    return (
        <main style={{ padding: '2rem' }}>
            <h1>Welcome to our Web Shop</h1>

            <div style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))',
                gap: '20px'
            }}>
                {products.map(product => (
                    <div key={product.id} style={{ border: '1px solid #ddd', padding: '1rem' }}>
                        <h3>{product.name}</h3>
                        <p>{product.price} RSD</p>
                        <button>Add to Cart</button>
                    </div>
                ))}
            </div>
        </main>
    );
};

export default Home;