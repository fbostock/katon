package fjdb.compactoidpuzzles;

public class GameTile {//extends Poolable
    //private ScoreBonus _scoreBonus;
    public static float destroyTime = 0.7f;

    public Integer type;

    // Start is called before the first frame update
    void Start() {

    }

    // Update is called once per frame
    void Update() {

    }

//    public void moveLeft() {
//        gameObject.transform.Translate(1, 0, 0);
//    }
//
//    public void moveRight() {
//        gameObject.transform.Translate(-1, 0, 0);
//
//    }
//
//    public void moveUp() {
//        gameObject.transform.Translate(0, 1, 0);
//
//    }
//
//    public void moveDown() {
//        gameObject.transform.Translate(0, -1, 0);
//    }
//
//    public void move(int x, int y) {
//        Vector3 transformPosition = gameObject.transform.position;
//        Debug.Log("Moving from " + transformPosition + " to (" + x + "," + y + ")");
//        gameObject.transform.Translate(x - transformPosition.x, y - transformPosition.y, 0);
//    }
//
//    public void setGlow(bool on) {
//        Material material = gameObject.GetComponent < Renderer > ().material;
//        if (on) {
//            material.EnableKeyword("_EMISSION");
//        } else {
//            material.DisableKeyword("_EMISSION");
//        }
//    }
//
    public void destroy() {
//        if (_scoreBonus != null) {
//            Destroy(_scoreBonus.gameObject);
//            _scoreBonus = null;
//        }
//        //reset the scale, in case object has been modified by animations etc.
//        gameObject.transform.localScale = new Vector3(1, 1, 1);
//        gameObject.transform.rotation = Quaternion.identity;
//        Poolable.TryPool(gameObject);
//
//        // Destroy(gameObject);
    }
//
//    public void playDestroy() {
//        GetComponent<Animator> ().Play("TileDestroy");
//    }
//
//    public void playHighlight() {
//        // playHighlightBorder(true);
//        GetComponent<Animator> ().Play("TileHighlight");
//    }
//
//    public void playHighlightBorder(bool turnOn) {
//        if (turnOn) {
//            GetComponent<Animator> ().SetTrigger("BorderHighlight");
//        } else {
//            GetComponent<Animator> ().SetTrigger("ResetBorderHL");
//            // GetComponent<Animator>().ResetTrigger("BorderHighlight");
//        }
//    }
//
//    public void playDisappear() {
//        GetComponent<Animator> ().Play("TileChange");
//    }
//
//    public void playAppear() {
//        GetComponent<Animator> ().Play("TileChange2");
//    }
//
//    public void addScoreBonus(ScoreBonus scoreBonus) {
//        _scoreBonus = scoreBonus;
//        scoreBonus.attach(this);
//    }
//
//    public void addScoreBonus(GameTile otherTile) {
//        _scoreBonus = otherTile.getScoreBonus();
//        otherTile._scoreBonus = null;
//        if (_scoreBonus != null) {
//            _scoreBonus.attach(this);
//        }
//    }
//
//    public ScoreBonus getScoreBonus() {
//        return _scoreBonus;
//    }
//
//    public int getScoreMultiplier() {
//        return _scoreBonus == null ? 1 : _scoreBonus.Multiplier;
//    }

}
