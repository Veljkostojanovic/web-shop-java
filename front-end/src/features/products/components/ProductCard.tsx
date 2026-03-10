import {Product} from "../../../types/product"
import {ShoppingCart} from "lucide-react"
interface Props {
    product: Product;
}


export const ProductCard = ({ product }: Props) => {
    return (
        <div className="h-full flex flex-col border rounded-xl overflow-hidden bg-white shadow-sm hover:shadow-md transition-all duration-300">

            <div className="aspect-square bg-gray-100 flex items-center justify-center border-b">
                <span className="text-gray-400">Image</span>
            </div>

            <div className="p-4 flex flex-col flex-grow">
        <span className="text-xs font-medium text-blue-600 uppercase mb-1">
          {product.categoryName || 'General'}
        </span>
                <h3 className="font-semibold text-gray-900 mb-2 line-clamp-1">
                    {product.name}
                </h3>
                <p className="text-sm text-gray-500 line-clamp-2 mb-4 flex-grow">
                    {product.description}
                </p>

                <div className="flex items-center justify-between mt-auto pt-4">
          <span className="text-lg font-bold text-gray-900">
            {product.price.toFixed(2)} RSD
          </span>
                    <button className="p-2 rounded-lg bg-gray-900 text-white hover:bg-gray-800 transition-colors">
                        <ShoppingCart size={18} />
                    </button>
                </div>
            </div>
        </div>
    );
};