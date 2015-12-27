var BookTable = React.createClass({
  render: function() {
    return (
      <table>
        <thead>
          <tr>
            <td>Name</td>
            <td>Size</td>
            <td>Notes</td>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td></td>
            <td></td>
            <td></td>
          </tr>
        </tbody>
      </table>
    )
  }
})

ReactDOM.render(
  <BookTable />,
  document.getElementById('content')
);
