const removeDuplicates = (array) => {
  let seen = {};
  return array.filter((item) => {
    let key = JSON.stringify(item);
    return seen.hasOwnProperty(key) ? false : (seen[key] = true);
  });
};

const cartReducer = (currentState, action) => {
  switch (action.type) {
    case 'ADD_TO_CART': {
      const combinedData = [...currentState['data'], ...action.payload];
      const uniqueData = removeDuplicates(combinedData);
      return {
        key: new Date().getTime(),
        data: uniqueData,
        refreshSeats: false,
      };
    }
    case 'WITH_CARGO': {
      const updatedData = currentState['data'].map((item) => {
        if (
          item['tripId'] === action['payload']['tripId'] &&
          item['seatId'] === action['payload']['seatId']
        ) {
          return { ...item, withCargo: action['payload']['withCargo'] };
        }
        return item;
      });
      return {
        key: new Date().getTime(),
        data: updatedData,
        refreshSeats: false,
      };
    }
    case 'DELETE_ITEM': {
      const updatedData = currentState['data'].filter(
        (item) => item.id !== action.payload.id
      );
      return {
        key: new Date().getTime(),
        data: updatedData,
        refreshSeats: true,
      };
    }
    case 'CLEAR_CART': {
      return {
        key: new Date().getTime(),
        data: [],
        refreshSeats: false,
      };
    }
    case 'TRIGGER_REFRESH_SEATS': {
      return {
        ...currentState,
        refreshSeats: false,
      };
    }
    default:
      return currentState;
  }
};

export default cartReducer;
