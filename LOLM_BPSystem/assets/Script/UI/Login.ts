import http from "../http";

const { ccclass, property } = cc._decorator;

@ccclass
export default class Login extends cc.Component {

    @property(cc.EditBox)
    Acc: cc.EditBox = null;
    @property(cc.EditBox)
    password: cc.EditBox = null;
    @property(cc.Node)
    ForgetPassword: cc.Node = null;
    onEnable()
    {
        var table: LoginData = new LoginData();
        table.acc = "this.Acc.string";
        table.pass = "this.password.string";
        http.ins.sendHttpPOST(table,this.GetLogin);
    }
    public GetLogin(data:JSON)
    {
        console.log("GetLogin data = "+data);
    }
    // update (dt) {}
}
export class LoginData {
    public pt: number = 0;
    public acc: String = "";
    public pass: String = "";
}