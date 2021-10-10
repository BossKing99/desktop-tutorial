
export default class GetTime {
    private static Difference: number = 0;
    public static Init(ServerTime: number) {
        GetTime.Difference = ServerTime - new Date().getTime();
    }
    public static GetTime(): number {
        return new Date().getTime() + GetTime.Difference;
    }
}
