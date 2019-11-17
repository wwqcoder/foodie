package cn.wwq.service;

import cn.wwq.pojo.Carousel;
import cn.wwq.pojo.UserAddress;
import cn.wwq.pojo.bo.AddressBO;

import java.util.List;

public interface AddressService {

    /**
     * 根据用户ID查询地址列表
     * @param userId
     * @return
     */
    public List<UserAddress> queryAll(String userId);

    /**
     * 添加地址列表
     * @param addressBO
     */
    public void addNewUserAddress(AddressBO addressBO);

    /**
     * 修改地址列表
     * @param addressBO
     */
    public void updateUserAddress(AddressBO addressBO);

    /**
     * 根据用户ID和地址ID，删除地址列表
     * @param userId
     * @param addressId
     */
    public void deleteUserAddress(String userId,String addressId);

    /**
     * 根据用户ID和地址ID ， 更新为默认地址
     * @param userId
     * @param addressId
     */
    public void updateUserAddressToBeDefault(String userId,String addressId);

    /**
     * 根据用户ID和地址ID ，查询具体用户的地址信息
     * @param userId
     * @param addressId
     * @return
     */
    public UserAddress queryUserAddress(String userId,String addressId);
}
