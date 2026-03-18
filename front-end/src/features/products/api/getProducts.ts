import apiClient from "../../../api/axiosConfig";
import type {Product} from '../../../types/product';

export const getProducts = async (): Promise<Product[]> => {
    const response = await apiClient.get<Product[]>('/products');
    return response.data;
};