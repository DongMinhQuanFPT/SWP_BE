//package com.SWP391.KoiXpress.Service;
//
//import com.SWP391.KoiXpress.Entity.Enum.MethodTransPort;
//import com.SWP391.KoiXpress.Entity.Enum.ProgressStatus;
//import com.SWP391.KoiXpress.Entity.Orders;
//import com.SWP391.KoiXpress.Entity.Vehicles;
//import com.SWP391.KoiXpress.Entity.WareHouses;
//import com.SWP391.KoiXpress.Exception.NotFoundException;
//import com.SWP391.KoiXpress.Model.request.Vehicle.CreateVehicleRequest;
//import com.SWP391.KoiXpress.Model.request.Vehicle.LoadOrderToVehicleRequest;
//import com.SWP391.KoiXpress.Model.request.Vehicle.UpdateVehicleRequest;
//import com.SWP391.KoiXpress.Model.response.Order.AllOrderResponse;
//import com.SWP391.KoiXpress.Model.response.Vehicle.AllVehicleResponse;
//import com.SWP391.KoiXpress.Model.response.Vehicle.CreateVehicleResponse;
//import com.SWP391.KoiXpress.Model.response.Vehicle.UpdateVehicleResponse;
//import com.SWP391.KoiXpress.Repository.OrderRepository;
//import com.SWP391.KoiXpress.Repository.VehicleRepository;
//import com.SWP391.KoiXpress.Repository.WareHouseRepository;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//public class VehicleService {
//
//    @Autowired
//    VehicleRepository vehicleRepository;
//
//    @Autowired
//    WareHouseRepository wareHouseRepository;
//
//    @Autowired
//    OrderRepository orderRepository;
//
//    @Autowired
//    ModelMapper modelMapper;
//
//    public List<CreateVehicleResponse> create(CreateVehicleRequest createVehicleRequest){
//        WareHouses wareHouses = wareHouseRepository.findById(createVehicleRequest.getWareHouseId())
//                .orElseThrow(()->new NotFoundException("Can not found warehouse"));
//        List<Vehicles> vehicles = new ArrayList<>();
//        for(int i = 0; i<createVehicleRequest.getQuantityVehicle();i++){
//            Vehicles vehicle = new Vehicles();
//            vehicle.setWareHouses(wareHouses);
//            vehicle.setVolume(createVehicleRequest.getVolume());
//            vehicles.add(vehicle);
//        }
//        vehicleRepository.saveAll(vehicles);
//        return vehicles
//                .stream()
//                .map(vehicle->modelMapper.map(vehicle,CreateVehicleResponse.class))
//                .toList();
//    }
//
//    public UpdateVehicleResponse update(UUID id, UpdateVehicleRequest updateVehicleRequest){
//        Vehicles vehicles = vehicleRepository.findByIdAndAvailableTrue(id).orElseThrow(()->new NotFoundException("Can not found vehicle"));
//        vehicles.setVolume(updateVehicleRequest.getVolume());
//        vehicleRepository.save(vehicles);
//        return modelMapper.map(vehicles, UpdateVehicleResponse.class);
//    }
//
//    public void delete(UUID id){
//        Vehicles vehicles = vehicleRepository.findByIdAndAvailableTrue(id).orElseThrow(()->new NotFoundException("Can not found vehicle"));
//        vehicles.setAvailable(false);
//        vehicleRepository.save(vehicles);
//    }
//
//    public List<AllVehicleResponse> getAllAvailableVehicle(){
//        return vehicleRepository.findAll()
//                .stream()
//                .filter(Vehicles::isAvailable)
//                .map(vehicles -> modelMapper.map(vehicles, AllVehicleResponse.class))
//                .toList();
//    }
//
//    public List<AllOrderResponse> loadOrderToVehicle(LoadOrderToVehicleRequest loadOrderToVehicleRequest) {
//
//        Vehicles vehicles = vehicleRepository.findByIdAndAvailableTrue(loadOrderToVehicleRequest.getVehicleId())
//                .orElseThrow(() -> new NotFoundException("Can not found vehicle"));
//
//        int volume = (int) vehicles.getVolume();
//
//        List<Orders> unPlaceOrders = orderRepository.findOrdersByWareHouseIdAndStatus(loadOrderToVehicleRequest.getWareHouseId(), ProgressStatus.EN_ROUTE)
//                .stream()
//                .filter(orders -> orders.getVehicles() == null)
//                .sorted(Comparator.comparing(Orders::getOrderDate))
//                .toList();
//
//        List<Orders> fastOrdersDelivery = unPlaceOrders.stream()
//                .filter(order -> order.getMethodTransPort() == MethodTransPort.FAST_DELIVERY)
//                .toList();
//
//        List<Orders> normalOrdersDelivery = unPlaceOrders.stream()
//                .filter(order -> order.getMethodTransPort() == MethodTransPort.NORMAL_DELIVERY)
//                .toList();
//
//        List<Orders> currentLoadOrder = new ArrayList<>(dynamicAlgorithm(fastOrdersDelivery, volume));
//
//        volume = (int) vehicles.getVolume() - getTotalVolume(currentLoadOrder);
//
//        currentLoadOrder.addAll(dynamicAlgorithm(normalOrdersDelivery,volume));
//
//        List<AllOrderResponse> allOrderResponses = currentLoadOrder.stream()
//                .map(orders -> modelMapper.map(orders, AllOrderResponse.class))
//                .toList();
//
//        orderRepository.saveAll(currentLoadOrder);
//
//        return allOrderResponses;
//    }
//
//    private List<Orders> dynamicAlgorithm(List<Orders> ordersList, int volume) {
//        int n = ordersList.size();
//        int[][] dp = new int[n + 1][volume + 1];
//
//
//        for (int i = 1; i <= n; i++) {
//            Orders orders = ordersList.get(i - 1);
//            for (int j = 1; j <= volume; j++) {
//                dp[i][j] = dp[i - 1][j];
//                if (j >= orders.getTotalVolume()) {
//                    dp[i][j] = Math.max(dp[i][j], dp[i - 1][j - (int) orders.getTotalVolume()] + orders.getTotalBox());
//                }
//            }
//        }
//
//        List<Orders> currentLoadOrder = new ArrayList<>();
//        for (int i = n; i > 0 && volume > 0; i--) {
//            if (dp[i][volume] != dp[i - 1][volume]) {
//                currentLoadOrder.add(ordersList.get(i - 1));
//                volume -= (int) ordersList.get(i - 1).getTotalVolume();
//            }
//        }
//
//        return currentLoadOrder;
//    }
//
//    private int getTotalVolume(List<Orders> ordersList) {
//        return ordersList.stream()
//                .mapToInt(order -> (int) order.getTotalVolume())
//                .sum();
//    }
//
//
//}
