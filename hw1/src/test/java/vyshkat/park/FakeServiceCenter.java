package vyshkat.park;

import vyshkat.interfaces.ServiceCenter;
import vyshkat.transport.Transport;

class FakeServiceCenter implements ServiceCenter {

    private boolean inspectionResult;

    public void setInspectionResult(boolean inspectionResult) {
        this.inspectionResult = inspectionResult;
    }

    @Override
    public boolean inspect(Transport transport) {
        return inspectionResult;
    }
}